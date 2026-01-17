package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.service.AdminProductService;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.dto.ProductAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductAdminResponse;
import com.cocinadelicia.backend.product.mapper.ProductAdminMapper;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdminProductServiceImpl implements AdminProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductAdminMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ProductAdminResponse> getProducts(
      Long categoryId, Boolean isActive, Pageable pageable) {

    Page<Product> page;

    if (categoryId != null && isActive != null) {
      page = productRepository.findByCategory_IdAndIsActive(categoryId, isActive, pageable);
    } else if (categoryId != null) {
      page = productRepository.findByCategory_Id(categoryId, pageable);
    } else if (isActive != null) {
      page = productRepository.findByIsActive(isActive, pageable);
    } else {
      page = productRepository.findAll(pageable);
    }

    Page<ProductAdminResponse> mapped = page.map(mapper::toResponse);
    return PageResponse.from(mapped);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductAdminResponse getById(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado: " + id));
    return mapper.toResponse(product);
  }

  @Override
  public ProductAdminResponse create(ProductAdminRequest request) {
    Category category =
        categoryRepository
            .findById(request.categoryId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "CATEGORY_NOT_FOUND", "Categoría no encontrada: " + request.categoryId()));

    Product product = mapper.toNewEntity(request, category);
    Product saved = productRepository.save(product);
    log.info("AdminProduct.create id={} name={}", saved.getId(), saved.getName());
    return mapper.toResponse(saved);
  }

  @Override
  public ProductAdminResponse update(Long id, ProductAdminRequest request) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado: " + id));

    Category category = null;
    if (request.categoryId() != null) {
      category =
          categoryRepository
              .findById(request.categoryId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "CATEGORY_NOT_FOUND",
                          "Categoría no encontrada: " + request.categoryId()));
    }

    mapper.updateEntityFromRequest(request, product, category);
    Product saved = productRepository.save(product);
    log.info("AdminProduct.update id={}", saved.getId());
    return mapper.toResponse(saved);
  }

  @Override
  public void delete(Long id) {
    productRepository.deleteById(id);
    log.info("AdminProduct.delete id={}", id);
  }
}
