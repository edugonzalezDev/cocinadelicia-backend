package com.cocinadelicia.backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para actualizar la cantidad de un item en el carrito.
 * Sprint S07 - US01
 */
@Schema(description = "Payload para actualizar la cantidad de un ítem en el carrito")
public record UpdateCartItemRequest(
    @Schema(description = "Nueva cantidad", example = "3", minimum = "1", maximum = "99")
        @NotNull(message = "quantity es obligatorio")
        @Min(value = 1, message = "quantity debe ser >= 1")
        @Max(value = 99, message = "quantity excede el máximo permitido (99)")
        Integer quantity) {}
