package com.cocinadelicia.backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * Response de un modificador dentro de un item de carrito.
 * Sprint S07 - US01
 */
@Schema(description = "Modificador seleccionado dentro de un ítem de carrito")
public record CartItemModifierResponse(
    @Schema(description = "ID de la opción de modificador", example = "101")
        Long modifierOptionId,
    @Schema(description = "Nombre de la opción", example = "Puré de papas") String optionName,
    @Schema(description = "Cantidad seleccionada", example = "1") int quantity,
    @Schema(description = "Precio adicional por unidad", example = "50.00")
        BigDecimal priceDelta,
    @Schema(description = "Total del modificador (priceDelta × quantity)", example = "50.00")
        BigDecimal total) {}
