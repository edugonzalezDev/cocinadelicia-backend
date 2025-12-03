// src/main/java/com/cocinadelicia/backend/product/dto/ProductVariantAdminRequest.java
package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para crear/actualizar variante de producto")
public record ProductVariantAdminRequest(
    String name, String sku, Boolean isActive, Boolean managesStock, Integer stockQuantity) {}
