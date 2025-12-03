// src/main/java/com/cocinadelicia/backend/product/mapper/ProductVariantMapper.java
package com.cocinadelicia.backend.product.mapper;

import com.cocinadelicia.backend.product.dto.ProductVariantAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import com.cocinadelicia.backend.product.model.ProductVariant;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {

  public ProductVariantAdminResponse toAdminResponse(ProductVariant entity) {
    return new ProductVariantAdminResponse(
        entity.getId(),
        entity.getName(),
        entity.getSku(),
        entity.isActive(),
        entity.isManagesStock(),
        entity.getStockQuantity());
  }

  public void updateEntityFromRequest(ProductVariantAdminRequest req, ProductVariant entity) {
    if (req.name() != null) entity.setName(req.name());
    if (req.sku() != null) entity.setSku(req.sku());
    if (req.isActive() != null) entity.setActive(req.isActive());
    if (req.managesStock() != null) entity.setManagesStock(req.managesStock());
    if (req.stockQuantity() != null) entity.setStockQuantity(req.stockQuantity());
  }

  public ProductVariant toNewEntity(ProductVariantAdminRequest req) {
    ProductVariant variant = new ProductVariant();
    variant.setName(req.name());
    variant.setSku(req.sku());
    variant.setActive(req.isActive() != null ? req.isActive() : true);
    variant.setManagesStock(req.managesStock() != null ? req.managesStock() : false);
    variant.setStockQuantity(req.stockQuantity() != null ? req.stockQuantity() : 0);
    return variant;
  }
}
