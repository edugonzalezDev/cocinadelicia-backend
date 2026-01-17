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

    boolean featured = variants.stream().anyMatch(ProductVariant::isFeatured);

    boolean dailyMenu = variants.stream().anyMatch(ProductVariant::isDailyMenu);

    boolean isNew = variants.stream().anyMatch(ProductVariant::isNew);

    return new ProductAdminResponse(
        entity.getId(),
        category != null ? category.getId() : null,
        category != null ? category.getName() : null,
        entity.getName(),
        entity.getSlug(),
        entity.getDescription(),
        entity.getTaxRatePercent(),
        entity.isActive(),
        variantsCount,
        featured,
        dailyMenu,
        isNew);
  }

  public void updateEntityFromRequest(ProductAdminRequest req, Product entity, Category category) {
    if (category != null) {
      entity.setCategory(category);
    }
    if (req.name() != null) entity.setName(req.name());
    if (req.slug() != null) entity.setSlug(req.slug());
    if (req.description() != null) entity.setDescription(req.description());
    if (req.taxRatePercent() != null) entity.setTaxRatePercent(req.taxRatePercent());
    if (req.isActive() != null) entity.setActive(req.isActive());
  }

  public Product toNewEntity(ProductAdminRequest req, Category category) {
    Product product = new Product();
    product.setCategory(category);
    product.setName(req.name());
    product.setSlug(req.slug());
    product.setDescription(req.description());
    product.setTaxRatePercent(req.taxRatePercent());
    product.setActive(req.isActive() != null ? req.isActive() : true);
    return product;
  }
}
