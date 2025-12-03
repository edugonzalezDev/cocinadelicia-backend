// src/main/java/com/cocinadelicia/backend/catalog/service/impl/CatalogServiceImpl.java
package com.cocinadelicia.backend.catalog.service.impl;

import com.cocinadelicia.backend.catalog.dto.*;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.model.Tag;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.service.PriceService;
import com.cocinadelicia.backend.product.service.dto.PriceInfo;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  private final PriceService priceService; // 👉 nuevo

  @Override
  public List<CategorySummaryResponse> getCategories() {
    var categories = categoryRepository.findAllByOrderByNameAsc();
    log.info("Catalog.getCategories count={}", categories.size());

    return categories.stream().map(this::toCategorySummary).toList();
  }

  @Override
  public PageResponse<ProductSummaryResponse> getProducts(CatalogFilter filter) {
    Sort sort =
      filter.sort() != null && filter.sort().isSorted()
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

    String shortDescription = buildShortDescription(product.getDescription());

    // Variantes activas con precio vigente
    List<CatalogVariantResponse> variants =
      product.getVariants() == null
        ? List.of()
        : product.getVariants().stream()
        .filter(ProductVariant::isActive)
        .map(this::toCatalogVariant) // Optional<CatalogVariantResponse>
        .flatMap(Optional::stream) // solo las que tienen precio vigente
        .toList();

    // fromPrice = mínimo de las variantes visibles
    MoneyResponse fromPrice =
      variants.stream()
        .map(CatalogVariantResponse::price)
        .filter(Objects::nonNull)
        .min(Comparator.comparing(MoneyResponse::amount))
        .orElse(null);

    // Disponibilidad a alto nivel
    boolean hasAvailableVariant =
      variants.stream()
        .anyMatch(
          v -> !"Sin stock".equalsIgnoreCase(v.availabilityLabel())); // cualquier disponible

    boolean managesStock =
      variants.stream().anyMatch(CatalogVariantResponse::managesStock);

    boolean madeToOrder =
      !variants.isEmpty()
        ? variants.stream().allMatch(v -> !v.managesStock())
        : true; // sin variantes => lo consideramos a pedido por defecto

    boolean available = product.isActive() && (variants.isEmpty() ? true : hasAvailableVariant);

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
      available,
      madeToOrder,
      managesStock,
      featured,
      dailyMenu,
      isNew,
      variants,
      tagSlugs);
  }

  private Optional<CatalogVariantResponse> toCatalogVariant(ProductVariant variant) {
    // Precio vigente; si no hay, no mostramos la variante
    Optional<PriceInfo> priceOpt = priceService.getCurrentPriceForVariant(variant.getId());
    if (priceOpt.isEmpty()) {
      return Optional.empty();
    }

    PriceInfo priceInfo = priceOpt.get();
    MoneyResponse price =
      new MoneyResponse(priceInfo.amount(), priceInfo.currency().name());

    String availabilityLabel = computeAvailabilityLabel(variant);

    CatalogVariantResponse dto =
      new CatalogVariantResponse(
        variant.getId(),
        variant.getName(),
        price,
        variant.isManagesStock(),
        variant.getStockQuantity(),
        availabilityLabel);

    return Optional.of(dto);
  }

  private String computeAvailabilityLabel(ProductVariant variant) {
    if (!variant.isActive()) {
      // En catálogo igual filtramos inactivos antes, pero por seguridad:
      return "Sin stock";
    }
    if (!variant.isManagesStock()) {
      // A pedido => siempre disponible
      return "Disponible";
    }
    return variant.getStockQuantity() > 0 ? "Disponible" : "Sin stock";
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
