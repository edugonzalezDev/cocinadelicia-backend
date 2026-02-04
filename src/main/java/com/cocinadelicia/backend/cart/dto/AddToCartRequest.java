package com.cocinadelicia.backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request para agregar un item al carrito.
 * Sprint S07 - US01
 */
@Schema(description = "Payload para agregar un ítem al carrito")
public record AddToCartRequest(
    @Schema(description = "ID del producto", example = "1", required = true)
        @NotNull(message = "productId es obligatorio")
        Long productId,
    @Schema(description = "ID de la variante del producto", example = "10", required = true)
        @NotNull(message = "productVariantId es obligatorio")
        Long productVariantId,
    @Schema(
            description = "Cantidad de unidades",
            example = "2",
            minimum = "1",
            maximum = "99",
            required = true)
        @NotNull(message = "quantity es obligatorio")
        @Min(value = 1, message = "quantity debe ser >= 1")
        @Max(value = 99, message = "quantity excede el máximo permitido (99)")
        Integer quantity,
    @Schema(
            description =
                "Modificadores seleccionados (opcional). Estructura: [{\"modifierOptionId\": 101, \"quantity\": 1}]",
            example = "[{\"modifierOptionId\": 101, \"quantity\": 1}]")
        @Valid
        List<CartItemModifierRequest> modifiers) {}
