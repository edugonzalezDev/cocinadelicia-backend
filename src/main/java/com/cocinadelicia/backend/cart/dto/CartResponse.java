package com.cocinadelicia.backend.cart.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response completo del carrito con todos sus items.
 * Sprint S07 - US01
 */
@Schema(description = "Carrito de compras completo")
public record CartResponse(
    @Schema(description = "ID del carrito", example = "1") Long cartId,
    @Schema(description = "ID del usuario propietario", example = "42") Long userId,
    @Schema(description = "Lista de items en el carrito") List<CartItemResponse> items,
    @Schema(description = "Cantidad total de items (suma de cantidades)", example = "5")
        int itemCount,
    @Schema(description = "Moneda del carrito", example = "UYU") CurrencyCode currency,
    @Schema(
            description = "Subtotal del carrito (suma de lineTotal de todos los items)",
            example = "2500.00")
        BigDecimal subtotal,
    @Schema(description = "Fecha de creación del carrito", example = "2026-02-04T10:30:00Z")
        Instant createdAt,
    @Schema(description = "Fecha de última actualización", example = "2026-02-04T11:15:00Z")
        Instant updatedAt) {}
