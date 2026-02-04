package com.cocinadelicia.backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request con un modificador dentro de un item de carrito.
 * Sprint S07 - US01
 */
@Schema(description = "Selección de un modificador dentro de un ítem de carrito")
public record CartItemModifierRequest(
    @Schema(description = "Id de la opción de modificador", example = "101", required = true)
        @NotNull(message = "modifierOptionId es obligatorio")
        Long modifierOptionId,
    @Schema(description = "Cantidad seleccionada para la opción", example = "1", minimum = "1")
        @Min(value = 1, message = "modifier quantity debe ser >= 1")
        @Max(value = 99, message = "modifier quantity excede el máximo permitido (99)")
        Integer quantity) {}
