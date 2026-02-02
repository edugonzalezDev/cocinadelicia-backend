package com.cocinadelicia.backend.catalog.mapper;

import com.cocinadelicia.backend.catalog.dto.*;
import com.cocinadelicia.backend.common.s3.CdnUrlBuilder;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductImage;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.model.Tag;
import com.cocinadelicia.backend.product.service.PriceService;
import com.cocinadelicia.backend.product.service.dto.PriceInfo;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class ProductCatalogMapper {

  private final CdnUrlBuilder cdnUrlBuilder;
  private final PriceService priceService;

  public CategorySummaryResponse toCategorySummary(Category category) {
    return new CategorySummaryResponse(
        category.getId(), category.getName(), category.getSlug(), category.getDescription());
  }

  public ProductSummaryResponse toProductSummary(Product product) {
    var category = product.getCategory();
    String mainImageUrl = resolveMainImageUrl(product);
    List<CatalogImageResponse> images = toImages(product);
    List<String> imageUrls = images.stream().map(CatalogImageResponse::url).toList();

    List<ProductVariant> activeVariants =
        product.getVariants() == null
            ? List.of()
            : product.getVariants().stream().filter(ProductVariant::isActive).toList();

    List<CatalogVariantResponse> variants =
        activeVariants.stream().map(this::toCatalogVariant).flatMap(Optional::stream).toList();

    MoneyResponse fromPrice = computeFromPrice(variants);

    boolean hasAvailableVariant =
        variants.stream().anyMatch(v -> !"Sin stock".equalsIgnoreCase(v.availabilityLabel()));
    boolean managesStock = variants.stream().anyMatch(CatalogVariantResponse::managesStock);
    boolean madeToOrder =
        !variants.isEmpty() ? variants.stream().allMatch(v -> !v.managesStock()) : true;
    boolean available = product.isActive() && (variants.isEmpty() || hasAvailableVariant);

    boolean featured = product.isFeatured();
    boolean dailyMenu = product.isDailyMenu();
    boolean isNew = product.isNew();

    List<String> tagSlugs = mapTags(product.getTags());

    return new ProductSummaryResponse(
        product.getId(),
        product.getName(),
        product.getSlug(),
        buildShortDescription(product.getDescription()),
        category != null ? category.getId() : null,
        category != null ? category.getName() : null,
        category != null ? category.getSlug() : null,
        mainImageUrl,
        imageUrls,
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

  public ProductDetailResponse toProductDetail(Product product) {
    var category = product.getCategory();
    List<ProductVariant> activeVariants =
        product.getVariants() == null
            ? List.of()
            : product.getVariants().stream().filter(ProductVariant::isActive).toList();

    List<CatalogVariantResponse> variants =
        activeVariants.stream().map(this::toCatalogVariant).flatMap(Optional::stream).toList();

    MoneyResponse fromPrice = computeFromPrice(variants);
    boolean hasAvailableVariant =
        variants.stream().anyMatch(v -> !"Sin stock".equalsIgnoreCase(v.availabilityLabel()));
    boolean managesStock = variants.stream().anyMatch(CatalogVariantResponse::managesStock);
    boolean madeToOrder =
        !variants.isEmpty() ? variants.stream().allMatch(v -> !v.managesStock()) : true;
    boolean available = product.isActive() && (variants.isEmpty() || hasAvailableVariant);

    boolean featured = product.isFeatured();
    boolean dailyMenu = product.isDailyMenu();
    boolean isNew = product.isNew();

    List<ModifierGroupCatalogResponse> modifierGroups = mapModifierGroups(activeVariants);

    return new ProductDetailResponse(
        product.getId(),
        product.getSlug(),
        product.getName(),
        product.getDescription(),
        buildShortDescription(product.getDescription()),
        category != null ? category.getId() : null,
        category != null ? category.getName() : null,
        category != null ? category.getSlug() : null,
        resolveMainImageUrl(product),
        toImages(product),
        fromPrice,
        variants,
        mapTags(product.getTags()),
        featured,
        dailyMenu,
        isNew,
        available,
        managesStock,
        madeToOrder,
        modifierGroups);
  }

  private MoneyResponse computeFromPrice(List<CatalogVariantResponse> variants) {
    return variants.stream()
        .map(CatalogVariantResponse::price)
        .filter(Objects::nonNull)
        .min(Comparator.comparing(MoneyResponse::amount))
        .orElse(null);
  }

  private List<CatalogImageResponse> toImages(Product product) {
    List<ProductImage> images = product.getImages() == null ? List.of() : product.getImages();
    if (images.isEmpty()) return List.of();

    Comparator<ProductImage> order =
        Comparator.comparing(ProductImage::isMain)
            .reversed()
            .thenComparingInt(ProductImage::getSortOrder)
            .thenComparing(
                ProductImage::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(ProductImage::getId, Comparator.nullsLast(Comparator.naturalOrder()));

    return images.stream()
        .filter(img -> img.getObjectKey() != null && !img.getObjectKey().isBlank())
        .sorted(order)
        .map(
            img ->
                new CatalogImageResponse(
                    img.getId(),
                    toPublicUrl(img.getObjectKey()),
                    img.isMain(),
                    img.getSortOrder(),
                    product.getName()))
        .filter(img -> img.url() != null && !img.url().isBlank())
        .toList();
  }

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

    return images.stream()
        .filter(img -> img.getObjectKey() != null && !img.getObjectKey().isBlank())
        .sorted(order)
        .map(ProductImage::getObjectKey)
        .map(this::toPublicUrl)
        .filter(url -> url != null && !url.isBlank())
        .findFirst()
        .orElse(null);
  }

  private Optional<CatalogVariantResponse> toCatalogVariant(ProductVariant variant) {
    Optional<PriceInfo> priceOpt = priceService.getCurrentPriceForVariant(variant.getId());
    if (priceOpt.isEmpty()) return Optional.empty();

    PriceInfo priceInfo = priceOpt.get();
    MoneyResponse price = new MoneyResponse(priceInfo.amount(), priceInfo.currency().name());
    String availabilityLabel = computeAvailabilityLabel(variant);

    // Mapear modifier groups para esta variante
    List<ModifierGroupCatalogResponse> modifiers =
        variant.getModifierGroups() == null
            ? List.of()
            : variant.getModifierGroups().stream()
                .filter(ModifierGroup::isActive)
                .sorted(
                    Comparator.comparingInt(ModifierGroup::getSortOrder)
                        .thenComparingLong(ModifierGroup::getId))
                .map(g -> toModifierGroupResponse(variant, g))
                .toList();

    return Optional.of(
        new CatalogVariantResponse(
            variant.getId(),
            variant.getName(),
            price,
            variant.isManagesStock(),
            variant.getStockQuantity(),
            availabilityLabel,
            modifiers));
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

  private List<String> mapTags(Set<Tag> tags) {
    return tags == null ? List.of() : tags.stream().map(Tag::getSlug).sorted().toList();
  }

  private List<ModifierGroupCatalogResponse> mapModifierGroups(List<ProductVariant> variants) {
    return variants.stream()
        .filter(v -> v.getModifierGroups() != null)
        .flatMap(v -> v.getModifierGroups().stream().map(g -> toModifierGroupResponse(v, g)))
        .filter(Objects::nonNull)
        .toList();
  }

  private ModifierGroupCatalogResponse toModifierGroupResponse(
      ProductVariant variant, ModifierGroup group) {
    if (group.getOptions() == null) return null;
    List<ModifierOptionCatalogResponse> options =
        group.getOptions().stream()
            .filter(ModifierOption::isActive)
            .sorted(
                Comparator.comparingInt(ModifierOption::getSortOrder)
                    .thenComparingLong(ModifierOption::getId))
            .map(this::toModifierOptionResponse)
            .toList();

    return new ModifierGroupCatalogResponse(
        group.getId(),
        variant.getId(),
        group.getName(),
        group.getMinSelect(),
        group.getMaxSelect(),
        group.getSelectionMode().name(),
        group.getRequiredTotalQty(),
        group.getDefaultOption() != null ? group.getDefaultOption().getId() : null,
        group.getSortOrder(),
        group.isActive(),
        options);
  }

  private ModifierOptionCatalogResponse toModifierOptionResponse(ModifierOption option) {
    return new ModifierOptionCatalogResponse(
        option.getId(),
        option.getName(),
        option.getSortOrder(),
        option.getPriceDelta(),
        option.getLinkedProductVariant() != null ? option.getLinkedProductVariant().getId() : null,
        option.isExclusive(),
        option.isActive());
  }

  private String toPublicUrl(String objectKey) {
    if (objectKey == null || objectKey.isBlank()) return null;
    // si ya es absoluta, devolvemos tal cual
    if (objectKey.startsWith("http://") || objectKey.startsWith("https://")) {
      return objectKey;
    }
    String url = cdnUrlBuilder.toPublicUrl(objectKey);
    if (url == null || url.isBlank()) {
      log.debug("cdnUrlBuilder returned blank for key={}", objectKey);
    }
    return url;
  }
}
