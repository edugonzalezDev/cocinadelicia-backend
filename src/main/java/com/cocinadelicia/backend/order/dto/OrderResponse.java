package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Respuesta estándar de un pedido")
public record OrderResponse(
    @Schema(description = "Id del pedido", example = "42") Long id,
    @Schema(
            description = "Estado actual del pedido",
            example = "CREATED",
            allowableValues = {
              "CREATED",
              "CONFIRMED",
              "PREPARING",
              "READY",
              "OUT_FOR_DELIVERY",
              "DELIVERED",
              "CANCELLED"
            })
        OrderStatus status,
    @Schema(
            description = "Tipo de cumplimiento del pedido",
            example = "DELIVERY",
            allowableValues = {"PICKUP", "DELIVERY"})
        FulfillmentType fulfillment,
    @Schema(description = "Moneda del pedido", example = "UYU") CurrencyCode currency,
    @Schema(description = "Subtotal del pedido", example = "900.00") BigDecimal subtotalAmount,
    @Schema(description = "Impuestos aplicados", example = "0.00") BigDecimal taxAmount,
    @Schema(description = "Descuentos aplicados", example = "0.00") BigDecimal discountAmount,
    @Schema(description = "Total final del pedido", example = "900.00") BigDecimal totalAmount,

    // Snapshot de envío
    @Schema(description = "Nombre de la persona que recibe el envío", example = "Juan Pérez")
        String shipName,
    @Schema(description = "Teléfono de contacto del envío", example = "099123456") String shipPhone,
    @Schema(description = "Dirección línea 1", example = "Av. Siempre Viva 742") String shipLine1,
    @Schema(description = "Dirección línea 2 (opcional)", example = "Apartamento 201")
        String shipLine2,
    @Schema(description = "Ciudad del envío", example = "Montevideo") String shipCity,
    @Schema(description = "Región / Departamento", example = "Canelones") String shipRegion,
    @Schema(description = "Código postal", example = "15000") String shipPostalCode,
    @Schema(
            description = "Referencia adicional para el envío",
            example = "Portón negro, timbre rojo")
        String shipReference,
    @Schema(description = "Notas del pedido", example = "Entregar entre 20:00 y 20:30")
        String notes,
    @Schema(
            description = "Fecha/hora deseada de entrega o retiro (UTC)",
            example = "2025-11-22T20:30:00Z")
        Instant requestedAt,
    @Schema(
            description =
                "Fecha/hora real de entrega (UTC), solo se completa cuando status = DELIVERED",
            example = "2025-11-22T20:52:00Z")
        Instant deliveredAt,
    @Schema(description = "Ítems del pedido") List<OrderItemResponse> items,
    @Schema(description = "Fecha de creación del pedido (UTC)", example = "2025-11-12T14:32:10Z")
        Instant createdAt,
    @Schema(
            description = "Fecha de última actualización del pedido (UTC)",
            example = "2025-11-12T15:10:00Z")
        Instant updatedAt) {}
