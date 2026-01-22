// src/main/java/com/cocinadelicia/backend/catalog/service/impl/CatalogServiceImpl.java
package com.cocinadelicia.backend.catalog.service.impl;

import com.cocinadelicia.backend.catalog.dto.*;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.s3.CdnUrlBuilder;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductImage;
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
  private final PriceService priceService;

  // ✅ MISMO builder que usa AdminProductImageServiceImpl
  private final CdnUrlBuilder cdnUrlBuilder;

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
        category.getId(), category.getName(), category.getSlug(), category.getDescription());
  }

  private ProductSummaryResponse toProductSummary(Product product) {
    var category = product.getCategory();

    String shortDescription = buildShortDescription(product.getDescription());

    // ✅ mainImageUrl usando CDN builder (igual que Admin)
    String mainImageUrl = resolveMainImageUrl(product);

    // Variantes activas a nivel dominio
    List<ProductVariant> activeDomainVariants =
        product.getVariants() == null
            ? List.of()
            : product.getVariants().stream().filter(ProductVariant::isActive).toList();

    // Variantes activas con precio vigente
    List<CatalogVariantResponse> variants =
        activeDomainVariants.stream()
            .map(this::toCatalogVariant)
            .flatMap(Optional::stream)
            .toList();

    MoneyResponse fromPrice =
        variants.stream()
            .map(CatalogVariantResponse::price)
            .filter(Objects::nonNull)
            .min(Comparator.comparing(MoneyResponse::amount))
            .orElse(null);

    boolean hasAvailableVariant =
        variants.stream().anyMatch(v -> !"Sin stock".equalsIgnoreCase(v.availabilityLabel()));

    boolean managesStock = variants.stream().anyMatch(CatalogVariantResponse::managesStock);

    boolean madeToOrder =
        !variants.isEmpty() ? variants.stream().allMatch(v -> !v.managesStock()) : true;

    boolean available = product.isActive() && (variants.isEmpty() || hasAvailableVariant);

    boolean featured = activeDomainVariants.stream().anyMatch(ProductVariant::isFeatured);
    boolean dailyMenu = activeDomainVariants.stream().anyMatch(ProductVariant::isDailyMenu);
    boolean isNew = activeDomainVariants.stream().anyMatch(ProductVariant::isNew);

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
        mainImageUrl,
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
    Optional<PriceInfo> priceOpt = priceService.getCurrentPriceForVariant(variant.getId());
    if (priceOpt.isEmpty()) return Optional.empty();

    PriceInfo priceInfo = priceOpt.get();
    MoneyResponse price = new MoneyResponse(priceInfo.amount(), priceInfo.currency().name());

    String availabilityLabel = computeAvailabilityLabel(variant);

    return Optional.of(
        new CatalogVariantResponse(
            variant.getId(),
            variant.getName(),
            price,
            variant.isManagesStock(),
            variant.getStockQuantity(),
            availabilityLabel));
  }

  private String computeAvailabilityLabel(ProductVariant variant) {
    if (!variant.isActive()) return "Sin stock";
    if (!variant.isManagesStock()) return "Disponible";
    return variant.getStockQuantity() > 0 ? "Disponible" : "Sin stock";
  }

  private String buildShortDescription(String fullDescription) {
    if (fullDescription == null || fullDescription.isBlank()) return null;
    String trimmed = fullDescription.trim();
    int maxLen = 140;
    if (trimmed.length() <= maxLen) return trimmed;
    return trimmed.substring(0, maxLen).trim() + "…";
  }

  // ---------- Images ----------

  /**
   * Regla: 1) Si hay una imagen main -> esa. 2) Si no, la primera por sortOrder asc, createdAt asc,
   * id asc. 3) Construimos URL pública con CdnUrlBuilder (igual que admin).
   */
  private String resolveMainImageUrl(Product product) {
    List<ProductImage> images = product.getImages() == null ? List.of() : product.getImages();
    if (images.isEmpty()) return null;

    Comparator<ProductImage> order =
        Comparator.comparing(ProductImage::isMain)
            .reversed()
            .thenComparingInt(ProductImage::getSortOrder)
            .thenComparing(
                ProductImage::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(ProductImage::getId, Comparator.nullsLast(Comparator.naturalOrder()));

    ProductImage chosen =
        images.stream()
            .filter(img -> img.getObjectKey() != null && !img.getObjectKey().isBlank())
            .sorted(order)
            .findFirst()
            .orElse(null);

    if (chosen == null) return null;

    String key = chosen.getObjectKey().trim();

    // Si ya viene absoluta, devolvela tal cual
    if (key.startsWith("http://") || key.startsWith("https://")) {
      return key;
    }

    // ✅ Igual que en admin
    String url = cdnUrlBuilder.toPublicUrl(key);
    return (url == null || url.isBlank()) ? null : url;
  }
}
