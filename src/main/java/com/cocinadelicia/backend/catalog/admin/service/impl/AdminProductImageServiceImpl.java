package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminPatchRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminResponse;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductImageService;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.s3.CdnUrlBuilder;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductImage;
import com.cocinadelicia.backend.product.repository.ProductImageRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdminProductImageServiceImpl implements AdminProductImageService {

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final CdnUrlBuilder cdnUrlBuilder;

  @Override
  @Transactional(readOnly = true)
  public List<ProductImageAdminResponse> listByProduct(long productId) {
    // valida producto
    productRepository
        .findById(productId)
        .orElseThrow(
            () ->
                new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado: " + productId));

    return productImageRepository
        .findByProduct_IdOrderBySortOrderAscCreatedAtAsc(productId)
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public ProductImageAdminResponse addToProduct(long productId, ProductImageAdminRequest req) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "PRODUCT_NOT_FOUND", "Producto no encontrado: " + productId));

    // (opcional pero recomendado) asegurar que la key pertenezca al producto
    String expectedPrefix = "products/" + productId + "/";
    if (req.objectKey() == null || !req.objectKey().startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("objectKey inválida. Debe empezar con: " + expectedPrefix);
    }

    boolean hasMain = productImageRepository.existsByProduct_IdAndIsMainTrue(productId);

    boolean requestMain = Boolean.TRUE.equals(req.isMain());
    boolean shouldBeMain =
        requestMain || !hasMain; // ✅ Regla: si no hay principal, la nueva pasa a main

    int sortOrder =
        req.sortOrder() != null ? Math.max(0, req.sortOrder()) : 0; // ✅ Regla: default 0

    if (shouldBeMain) {
      // ✅ swap: si viene isMain=true (o auto), apagamos otras
      var current =
          productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(productId);
      for (var img : current) {
        if (img.isMain()) img.setMain(false);
      }
    }

    ProductImage entity =
        ProductImage.builder()
            .product(product)
            .objectKey(req.objectKey())
            .isMain(shouldBeMain)
            .sortOrder(sortOrder)
            .build();

    ProductImage saved = productImageRepository.save(entity);
    log.info(
        "AdminProductImage.add productId={} imageId={} main={}",
        productId,
        saved.getId(),
        saved.isMain());

    return toDto(saved);
  }

  @Override
  public ProductImageAdminResponse patch(long imageId, ProductImageAdminPatchRequest req) {
    ProductImage img =
        productImageRepository
            .findById(imageId)
            .orElseThrow(
                () -> new NotFoundException("IMAGE_NOT_FOUND", "Imagen no encontrada: " + imageId));

    long productId = img.getProduct().getId();

    if (req.sortOrder() != null) {
      img.setSortOrder(Math.max(0, req.sortOrder()));
    }

    if (Boolean.TRUE.equals(req.isMain())) {
      // ✅ swap principal
      var all = productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(productId);
      for (var it : all) {
        it.setMain(it.getId().equals(img.getId()));
      }
    }

    ProductImage saved = productImageRepository.save(img);
    return toDto(saved);
  }

  @Override
  public void delete(long imageId) {
    ProductImage img =
        productImageRepository
            .findById(imageId)
            .orElseThrow(
                () -> new NotFoundException("IMAGE_NOT_FOUND", "Imagen no encontrada: " + imageId));

    long productId = img.getProduct().getId();
    boolean wasMain = img.isMain();

    productImageRepository.delete(img); // SQLDelete -> soft delete
    log.info(
        "AdminProductImage.delete imageId={} productId={} wasMain={}", imageId, productId, wasMain);

    if (wasMain) {
      // ✅ si borramos la main, elegimos otra como main (si existe)
      var remaining =
          productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(productId);
      if (!remaining.isEmpty()) {
        remaining.get(0).setMain(true);
        for (int i = 1; i < remaining.size(); i++) remaining.get(i).setMain(false);
        productImageRepository.saveAll(remaining);
      }
    }
  }

  private ProductImageAdminResponse toDto(ProductImage img) {
    return new ProductImageAdminResponse(
        img.getId(),
        img.getObjectKey(),
        cdnUrlBuilder.toPublicUrl(img.getObjectKey()),
        img.isMain(),
        img.getSortOrder());
  }
}
