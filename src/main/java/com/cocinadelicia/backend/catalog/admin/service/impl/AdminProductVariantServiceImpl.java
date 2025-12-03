package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.service.AdminProductVariantService;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import com.cocinadelicia.backend.product.mapper.ProductVariantMapper;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdminProductVariantServiceImpl implements AdminProductVariantService {

  private final ProductVariantRepository variantRepository;
  private final ProductRepository productRepository;
  private final ProductVariantMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<ProductVariantAdminResponse> getByProductId(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "PRODUCT_NOT_FOUND", "Producto no encontrado: " + productId));

    List<ProductVariant> list = product.getVariants();
    return list.stream().map(mapper::toAdminResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ProductVariantAdminResponse getById(Long id) {
    ProductVariant variant =
        variantRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada: " + id));
    return mapper.toAdminResponse(variant);
  }

  @Override
  public ProductVariantAdminResponse create(Long productId, ProductVariantAdminRequest request) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "PRODUCT_NOT_FOUND", "Producto no encontrado: " + productId));

    ProductVariant variant = mapper.toNewEntity(request);
    variant.setProduct(product);

    ProductVariant saved = variantRepository.save(variant);
    log.info("AdminVariant.create id={} productId={}", saved.getId(), productId);
    return mapper.toAdminResponse(saved);
  }

  @Override
  public ProductVariantAdminResponse update(Long id, ProductVariantAdminRequest request) {
    ProductVariant variant =
        variantRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada: " + id));

    mapper.updateEntityFromRequest(request, variant);
    ProductVariant saved = variantRepository.save(variant);
    log.info("AdminVariant.update id={}", saved.getId());
    return mapper.toAdminResponse(saved);
  }

  @Override
  public void delete(Long id) {
    variantRepository.deleteById(id);
    log.info("AdminVariant.delete id={}", id);
  }
}
