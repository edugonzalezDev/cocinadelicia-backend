package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para actualizar el estado de un pedido")
public record UpdateOrderStatusRequest(

  @Schema(
    description = "Nuevo estado del pedido",
    example = "PREPARING",
    allowableValues = {
      "CREATED", "CONFIRMED", "PREPARING", "READY",
      "OUT_FOR_DELIVERY", "DELIVERED", "CANCELED"
    }
  )
  OrderStatus status,

  @Schema(
    description = "Nota opcional para auditoría o contexto del cambio",
    example = "Cliente llamó y pidió prioridad por horario"
  )
  String note
) {}
