package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(description = "Respuesta completa de orden para administradores (incluye datos sensibles)")
public record OrderAdminResponse(
    @Schema(description = "Id del pedido", example = "42") Long id,
    @Schema(description = "ID del usuario que creó la orden", example = "123") Long userId,
    @Schema(description = "Email del usuario", example = "cliente@example.com") String userEmail,
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
    @Schema(description = "Fecha/hora solicitada de entrega") Instant requestedAt,
    @Schema(description = "Fecha/hora real de entrega") Instant deliveredAt,
    @Schema(description = "Email del chef asignado") String assignedChefEmail,
    @Schema(description = "Ítems del pedido") List<OrderItemResponse> items,
    @Schema(description = "Fecha de creación") Instant createdAt,
    @Schema(description = "Fecha de última actualización") Instant updatedAt,
    @Schema(description = "Fecha de eliminación (soft delete)") Instant deletedAt,
    @Schema(description = "Usuario que eliminó la orden") String deletedBy,
    @Schema(description = "Historial de cambios de estado")
        List<StatusHistoryEntry> statusHistory) {

  @Schema(description = "Entrada del historial de cambios de estado")
  public record StatusHistoryEntry(
      @Schema(description = "Estado anterior") OrderStatus fromStatus,
      @Schema(description = "Estado nuevo") OrderStatus toStatus,
      @Schema(description = "Usuario que realizó el cambio") String changedBy,
      @Schema(description = "Fecha/hora del cambio") Instant changedAt,
      @Schema(description = "Razón del cambio") String reason) {}
}
