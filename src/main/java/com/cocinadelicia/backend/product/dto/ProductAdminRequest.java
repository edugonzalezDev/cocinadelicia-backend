package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Payload para crear/actualizar producto (admin)")
public record ProductAdminRequest(
    @Schema(description = "ID de la categoría asociada", example = "1") Long categoryId,
    @Schema(description = "Nombre del producto", example = "Empanadas surtidas") String name,
    @Schema(description = "Slug único del producto", example = "empanadas-surtidas") String slug,
    @Schema(
            description = "Descripción larga del producto",
            example = "Docena de empanadas surtidas caseras")
        String description,
    @Schema(description = "Tasa de impuesto en porcentaje", example = "22.00")
        BigDecimal taxRatePercent,
    @Schema(description = "Si el producto está activo o no", example = "true") Boolean isActive) {}
