// src/main/java/com/cocinadelicia/backend/catalog/service/impl/CatalogServiceImpl.java
package com.cocinadelicia.backend.catalog.service.impl;

import com.cocinadelicia.backend.catalog.dto.*;
import com.cocinadelicia.backend.catalog.mapper.ProductCatalogMapper;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.spec.ProductSpecifications;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final ProductCatalogMapper productCatalogMapper;

  @Override
  public List<CategorySummaryResponse> getCategories() {
    var categories = categoryRepository.findAllByOrderByNameAsc();
    log.info("Catalog.getCategories count={}", categories.size());
    return categories.stream().map(productCatalogMapper::toCategorySummary).toList();
  }

  @Override
  public PageResponse<ProductSummaryResponse> getProducts(CatalogFilter filter) {
    Sort sort =
        filter.sort() != null && filter.sort().isSorted()
            ? filter.sort()
            : Sort.by(Sort.Direction.ASC, "name");

    Pageable pageable = PageRequest.of(filter.page(), filter.size(), sort);

    Specification<Product> spec = buildSpecification(filter);

    log.info(
        "Catalog.getProducts RECIBIDO → searchQuery={} categorySlug={} tagSlugs={} availableOnly={} featured={} dailyMenu={} isNew={} page={} size={}",
        filter.searchQuery(),
        filter.categorySlug(),
        filter.tagSlugs(),
        filter.availableOnly(),
        filter.featured(),
        filter.dailyMenu(),
        filter.isNew(),
        filter.page(),
        filter.size());

    Page<Product> page = productRepository.findAll(spec, pageable);

    log.debug(
        "Catalog.getProducts RESULTADO → totalElements={} totalPages={} currentPage={} hasContent={}",
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.hasContent());

    Page<ProductSummaryResponse> mapped = page.map(productCatalogMapper::toProductSummary);
    return PageResponse.from(mapped);
  }

  @Override
  public ProductDetailResponse getProductBySlug(String slug) {
    log.info("Catalog.getProductBySlug slug={}", slug);
    Product product =
        productRepository
            .findBySlugIgnoreCaseAndIsActiveTrue(slug)
            .orElseThrow(
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado."));
    return productCatalogMapper.toProductDetail(product);
  }

  private Specification<Product> buildSpecification(CatalogFilter filter) {
    Specification<Product> spec = ProductSpecifications.isActive();

    if (filter.searchQuery() != null && !filter.searchQuery().isBlank()) {
      spec = spec.and(ProductSpecifications.searchText(filter.searchQuery()));
      log.debug("Aplicado filtro: searchText='{}'", filter.searchQuery());
    }

    if (filter.categorySlug() != null && !filter.categorySlug().isBlank()) {
      spec = spec.and(ProductSpecifications.hasCategory(filter.categorySlug()));
      log.debug("Aplicado filtro: categorySlug='{}'", filter.categorySlug());
    }

    if (filter.tagSlugs() != null && !filter.tagSlugs().isEmpty()) {
      spec = spec.and(ProductSpecifications.hasTags(filter.tagSlugs()));
      log.debug("Aplicado filtro: tagSlugs={}", filter.tagSlugs());
    }

    if (Boolean.TRUE.equals(filter.availableOnly())) {
      spec = spec.and(ProductSpecifications.hasAvailableVariant());
      log.debug("Aplicado filtro: availableOnly=true");
    }

    if (Boolean.TRUE.equals(filter.featured())) {
      spec = spec.and(ProductSpecifications.hasFeaturedVariant());
      log.debug("Aplicado filtro: featured=true");
    }

    if (Boolean.TRUE.equals(filter.dailyMenu())) {
      spec = spec.and(ProductSpecifications.hasDailyMenuVariant());
      log.debug("Aplicado filtro: dailyMenu=true");
    }

    if (Boolean.TRUE.equals(filter.isNew())) {
      spec = spec.and(ProductSpecifications.hasNewVariant());
      log.debug("Aplicado filtro: isNew=true");
    }

    return spec;
  }
}
