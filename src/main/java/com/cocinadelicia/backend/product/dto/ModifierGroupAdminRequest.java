package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para crear o actualizar un grupo de modificadores")
public record ModifierGroupAdminRequest(
    @Schema(description = "Id de la variante a la que pertenece el grupo", example = "10")
        @NotNull(message = "productVariantId es obligatorio") Long productVariantId,
    @Schema(description = "Nombre del grupo", example = "Guarniciones")
        @NotBlank(message = "name es obligatorio") String name,
    @Schema(description = "Mínimo de opciones a elegir", example = "0")
        @Min(value = 0, message = "minSelect debe ser >= 0") Integer minSelect,
    @Schema(description = "Máximo de opciones a elegir", example = "2")
        @Min(value = 0, message = "maxSelect debe ser >= 0") Integer maxSelect,
    @Schema(description = "Modo de selección: SINGLE | MULTI | QTY", example = "MULTI")
        String selectionMode,
    @Schema(description = "Cantidad total requerida (solo para QTY)", example = "6")
        Integer requiredTotalQty,
    @Schema(description = "Opción por defecto", example = "101") Long defaultOptionId,
    @Schema(description = "Orden de despliegue", example = "0") Integer sortOrder,
    @Schema(description = "Activo", example = "true") Boolean active) {}
