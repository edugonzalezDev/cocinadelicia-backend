// src/main/java/com/cocinadelicia/backend/product/dto/ProductVariantAdminResponse.java
package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Variante de producto para administración")
public record ProductVariantAdminResponse(
  Long id,
  String name,
  String sku,
  boolean isActive,
  boolean managesStock,
  int stockQuantity
) {}
