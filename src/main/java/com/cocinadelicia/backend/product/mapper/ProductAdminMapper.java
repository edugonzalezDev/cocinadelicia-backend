package com.cocinadelicia.backend.product.mapper;

import com.cocinadelicia.backend.product.dto.ProductAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductAdminResponse;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductAdminMapper {

  public ProductAdminResponse toResponse(Product entity) {
    if (entity == null) return null;

    Category category = entity.getCategory();
    List<ProductVariant> variants = entity.getVariants() != null ? entity.getVariants() : List.of();
    int variantsCount = variants.size();

    return new ProductAdminResponse(
        entity.getId(),
        category != null ? category.getId() : null,
        category != null ? category.getName() : null,
        entity.getName(),
        entity.getSlug(),
        entity.getDescription(),
        entity.getTaxRatePercent(),
        entity.isActive(),
        entity.getUpdatedAt(),
        variantsCount,
        entity.isFeatured(),
        entity.isDailyMenu(),
        entity.isNew());
  }

  public void updateEntityFromRequest(ProductAdminRequest req, Product entity, Category category) {
    if (category != null) {
      entity.setCategory(category);
    }
    if (req.name() != null) entity.setName(normalizeText(req.name()));
    if (req.slug() != null) entity.setSlug(normalizeSlug(req.slug()));
    if (req.description() != null) entity.setDescription(normalizeText(req.description()));
    if (req.taxRatePercent() != null) entity.setTaxRatePercent(req.taxRatePercent());
    if (req.isActive() != null) entity.setActive(req.isActive());
    if (req.isFeatured() != null) entity.setFeatured(req.isFeatured());
    if (req.isDailyMenu() != null) entity.setDailyMenu(req.isDailyMenu());
    if (req.isNew() != null) entity.setNew(req.isNew());
  }

  public Product toNewEntity(ProductAdminRequest req, Category category) {
    Product product = new Product();
    product.setCategory(category);
    product.setName(normalizeText(req.name()));
    product.setSlug(normalizeSlug(req.slug()));
    product.setDescription(normalizeText(req.description()));
    product.setTaxRatePercent(req.taxRatePercent());
    product.setActive(req.isActive() != null ? req.isActive() : true);
    product.setFeatured(req.isFeatured() != null ? req.isFeatured() : false);
    product.setDailyMenu(req.isDailyMenu() != null ? req.isDailyMenu() : false);
    product.setNew(req.isNew() != null ? req.isNew() : false);
    return product;
  }

  private String normalizeText(String value) {
    return value == null ? null : value.trim();
  }

  private String normalizeSlug(String value) {
    return value == null ? null : value.trim().toLowerCase();
  }
}
