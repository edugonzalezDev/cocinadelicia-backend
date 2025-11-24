package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Ítem del pedido en la respuesta")
public record OrderItemResponse(
    @Schema(description = "Id interno del ítem de pedido", example = "101") Long id,
    @Schema(description = "Id del producto", example = "1") Long productId,
    @Schema(description = "Id de la variante del producto", example = "10") Long productVariantId,
    @Schema(description = "Nombre del producto", example = "Pizza Muzarella") String productName,
    @Schema(description = "Nombre de la variante", example = "Grande") String variantName,
    @Schema(description = "Precio unitario de la variante", example = "450.00")
        BigDecimal unitPrice,
    @Schema(description = "Cantidad de unidades", example = "2") int quantity,
    @Schema(description = "Total de la línea (unitPrice × quantity)", example = "900.00")
        BigDecimal lineTotal) {}
