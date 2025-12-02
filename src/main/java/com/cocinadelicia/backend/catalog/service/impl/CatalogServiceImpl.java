// src/main/java/com/cocinadelicia/backend/catalog/service/CatalogServiceImpl.java
package com.cocinadelicia.backend.catalog.service.impl;

import com.cocinadelicia.backend.catalog.dto.CatalogFilter;
import com.cocinadelicia.backend.catalog.dto.CategorySummaryResponse;
import com.cocinadelicia.backend.catalog.dto.ProductSummaryResponse;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.Tag;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public List<CategorySummaryResponse> getCategories() {
    var categories = categoryRepository.findAllByOrderByNameAsc();
    log.info("Catalog.getCategories count={}", categories.size());

    return categories.stream().map(this::toCategorySummary).toList();
  }

  @Override
  public PageResponse<ProductSummaryResponse> getProducts(CatalogFilter filter) {
    Sort sort = filter.sort() != null && filter.sort().isSorted()
      ? filter.sort()
      : Sort.by(Sort.Direction.ASC, "name");

    Pageable pageable = PageRequest.of(filter.page(), filter.size(), sort);

    boolean hasCategory = filter.categorySlug() != null && !filter.categorySlug().isBlank();

    Page<Product> page;
    if (hasCategory) {
      log.info(
        "Catalog.getProducts categorySlug={} page={} size={}",
        filter.categorySlug(),
        filter.page(),
        filter.size());
      page =
        productRepository.findByIsActiveTrueAndCategory_SlugIgnoreCase(
          filter.categorySlug(), pageable);
    } else {
      log.info("Catalog.getProducts ALL page={} size={}", filter.page(), filter.size());
      page = productRepository.findByIsActiveTrue(pageable);
    }

    Page<ProductSummaryResponse> mapped = page.map(this::toProductSummary);
    return PageResponse.from(mapped);
  }


  // ---------- Mappers ----------

  private CategorySummaryResponse toCategorySummary(Category category) {
    return new CategorySummaryResponse(
      category.getId(),
      category.getName(),
      category.getSlug(),
      category.getDescription());
  }

  private ProductSummaryResponse toProductSummary(Product product) {
    var category = product.getCategory();

    // Por ahora: shortDescription = descripción truncada (opcional).
    String shortDescription = buildShortDescription(product.getDescription());

    // TODO (sprint futuro): calcular fromPrice a partir de variantes / price_history.
    BigDecimal fromPrice = null;
    String currency = "UYU"; // por ahora fijo; luego podés inferir desde PriceHistory/CurrencyCode

    // Disponibilidad/flags: por ahora valores derivados/simplificados.
    boolean available = product.isActive(); // activo = disponible
    boolean madeToOrder = true;            // asumimos a pedido por defecto
    boolean managesStock = false;          // stock real vendrá más adelante

    boolean featured = false;
    boolean dailyMenu = false;
    boolean isNew = false;

    List<String> tagSlugs =
      product.getTags() == null
        ? List.of()
        : product.getTags().stream().map(Tag::getSlug).sorted().toList();

    return new ProductSummaryResponse(
      product.getId(),
      product.getName(),
      product.getSlug(),
      shortDescription,
      category != null ? category.getId() : null,
      category != null ? category.getName() : null,
      category != null ? category.getSlug() : null,
      null, // mainImageUrl → se completará cuando integremos imágenes
      fromPrice,
      currency,
      available,
      madeToOrder,
      managesStock,
      featured,
      dailyMenu,
      isNew,
      tagSlugs);
  }

  private String buildShortDescription(String fullDescription) {
    if (fullDescription == null || fullDescription.isBlank()) {
      return null;
    }
    String trimmed = fullDescription.trim();
    int maxLen = 140;
    if (trimmed.length() <= maxLen) {
      return trimmed;
    }
    return trimmed.substring(0, maxLen).trim() + "…";
  }
}
