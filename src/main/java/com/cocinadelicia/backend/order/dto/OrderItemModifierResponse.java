package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Modificador aplicado a un ítem del pedido")
public record OrderItemModifierResponse(
    @Schema(description = "Id interno del modificador en el pedido", example = "501") Long id,
    @Schema(description = "Id de la opción seleccionada", example = "101") Long modifierOptionId,
    @Schema(description = "Nombre de la opción al momento de la compra", example = "Puré de papas")
        String optionName,
    @Schema(description = "Cantidad seleccionada de la opción", example = "1") int quantity,
    @Schema(description = "Precio delta tomado como snapshot", example = "50.00")
        BigDecimal priceDeltaSnapshot,
    @Schema(description = "Precio unitario de la variante linkeada (si aplica)", example = "120.00")
        BigDecimal unitPriceSnapshot,
    @Schema(description = "Total de la opción (snapshot)", example = "120.00")
        BigDecimal totalPriceSnapshot,
    @Schema(description = "Id de la variante linkeada (snapshot)", example = "12")
        Long linkedProductVariantIdSnapshot) {}
