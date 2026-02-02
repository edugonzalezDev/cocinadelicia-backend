package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para actualizar el estado de un pedido (Admin)")
public record UpdateOrderStatusAdminRequest(
    @Schema(
            description = "Nuevo estado del pedido",
            example = "PREPARING",
            allowableValues = {
              "CREATED",
              "CONFIRMED",
              "PREPARING",
              "READY",
              "OUT_FOR_DELIVERY",
              "DELIVERED",
              "CANCELLED"
            })
        @NotNull(message = "El estado es obligatorio") OrderStatus status,
    @Schema(
            description = "Razón opcional del cambio",
            example = "Cliente pidió adelantar el pedido")
        String reason) {}
