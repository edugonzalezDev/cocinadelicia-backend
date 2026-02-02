package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Selección de un modificador dentro de un ítem")
public record OrderItemModifierRequest(
    @Schema(description = "Id de la opción de modificador", example = "101")
        @NotNull(message = "modifierOptionId es obligatorio") Long modifierOptionId,
    @Schema(description = "Cantidad seleccionada para la opción", example = "1", minimum = "1")
        @Min(value = 1, message = "modifier quantity debe ser >= 1") @Max(value = 99, message = "modifier quantity excede el máximo permitido (99)")
        Integer quantity) {}
