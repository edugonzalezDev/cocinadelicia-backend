package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Producto para administración")
public record ProductAdminResponse(
    Long id,
    Long categoryId,
    String categoryName,
    String name,
    String slug,
    String description,
    BigDecimal taxRatePercent,
    boolean isActive,
    Instant updatedAt,
    int variantsCount,
    boolean isFeatured,
    boolean isDailyMenu,
    boolean isNew) {}
