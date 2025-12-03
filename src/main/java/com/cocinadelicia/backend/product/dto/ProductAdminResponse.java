package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

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
    int variantsCount,
    boolean featured,
    boolean dailyMenu,
    boolean isNew) {}
