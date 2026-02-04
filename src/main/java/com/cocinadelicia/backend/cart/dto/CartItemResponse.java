package com.cocinadelicia.backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * Response de un item individual dentro del carrito.
 * Sprint S07 - US01
 */
@Schema(description = "Ítem individual dentro del carrito")
public record CartItemResponse(
    @Schema(description = "ID interno del ítem", example = "1") Long id,
    @Schema(description = "ID del producto", example = "10") Long productId,
    @Schema(description = "Nombre del producto", example = "Milanesa clásica") String productName,
    @Schema(description = "ID de la variante", example = "20") Long productVariantId,
    @Schema(description = "Nombre de la variante", example = "Grande") String variantName,
    @Schema(description = "Cantidad de unidades", example = "2") int quantity,
    @Schema(description = "Precio unitario actual de la variante", example = "450.00")
        BigDecimal unitPrice,
    @Schema(description = "Total de la línea base (unitPrice × quantity)", example = "900.00")
        BigDecimal baseLineTotal,
    @Schema(
            description =
                "Total de modifiers para este item (suma de priceDelta × quantity de cada modifier)",
            example = "100.00")
        BigDecimal modifiersTotal,
    @Schema(
            description = "Total de la línea incluyendo modifiers (baseLineTotal + modifiersTotal)",
            example = "1000.00")
        BigDecimal lineTotal,
    @Schema(description = "Modificadores seleccionados (puede ser null si no hay)")
        List<CartItemModifierResponse> modifiers) {}
