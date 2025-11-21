package com.cocinadelicia.backend.order.events;

import com.cocinadelicia.backend.order.dto.OrderResponse;

/**
 * Envoltura estándar para eventos WebSocket.
 *
 * type: tipo de evento, ej: "ORDER_UPDATED"
 * payload: contenido del evento, en nuestro caso un OrderResponse
 */
public record OrderEventPayload(
  String type,
  OrderResponse payload
) {}
