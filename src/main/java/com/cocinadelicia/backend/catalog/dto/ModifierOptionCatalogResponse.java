package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Opción de un grupo de modificadores")
public record ModifierOptionCatalogResponse(
    @Schema(description = "Id de la opción", example = "101") Long id,
    @Schema(description = "Nombre visible de la opción", example = "Puré de papas") String name,
    @Schema(description = "Orden de despliegue", example = "1") int sortOrder,
    @Schema(description = "Precio adicional de la opción", example = "50.00") BigDecimal priceDelta,
    @Schema(description = "Id de la variante linkeada si aplica", example = "12")
        Long linkedProductVariantId,
    @Schema(description = "Indica si la opción es exclusiva dentro del grupo", example = "false")
        boolean exclusive,
    @Schema(description = "Indica si la opción está activa", example = "true") boolean active) {}
