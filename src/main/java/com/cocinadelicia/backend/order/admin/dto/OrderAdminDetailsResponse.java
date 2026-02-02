package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Detalle completo de una orden para Admin")
public record OrderAdminDetailsResponse(
    @Schema(description = "Id del pedido", example = "42") Long id,
    @Schema(description = "Estado actual del pedido", example = "PREPARING") OrderStatus status,
    @Schema(description = "Tipo de cumplimiento", example = "DELIVERY") FulfillmentType fulfillment,
    @Schema(description = "Moneda", example = "UYU") CurrencyCode currency,
    @Schema(description = "Subtotal", example = "900.00") BigDecimal subtotalAmount,
    @Schema(description = "Impuestos", example = "0.00") BigDecimal taxAmount,
    @Schema(description = "Descuentos", example = "0.00") BigDecimal discountAmount,
    @Schema(description = "Total final", example = "900.00") BigDecimal totalAmount,
    @Schema(description = "Nombre de envío") String shipName,
    @Schema(description = "Teléfono de envío") String shipPhone,
    @Schema(description = "Dirección línea 1") String shipLine1,
    @Schema(description = "Dirección línea 2") String shipLine2,
    @Schema(description = "Ciudad") String shipCity,
    @Schema(description = "Región/Departamento") String shipRegion,
    @Schema(description = "Código postal") String shipPostalCode,
    @Schema(description = "Referencia de envío") String shipReference,
    @Schema(description = "Notas del pedido") String notes,
    @Schema(description = "Email del chef asignado") String assignedChefEmail,
    @Schema(description = "Fecha/hora solicitada de entrega") Instant requestedAt,
    @Schema(description = "Fecha/hora real de entrega") Instant deliveredAt,
    @Schema(description = "Ítems del pedido") List<OrderItemResponse> items,
    @Schema(description = "Fecha de creación") Instant createdAt,
    @Schema(description = "Fecha de última actualización") Instant updatedAt,
    @Schema(description = "Fecha de eliminación (soft delete)") Instant deletedAt,
    @Schema(description = "Usuario que eliminó la orden") String deletedBy) {}
