package com.cocinadelicia.backend.order.events;

import com.cocinadelicia.backend.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica eventos WebSocket hacia /topic/orders. Todos los eventos usan estructura: { type: "...",
 * payload: OrderResponse }
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class OrderWebSocketPublisher {

  public static final String ORDERS_TOPIC = "/topic/orders";

  private final SimpMessagingTemplate messagingTemplate;

  /** Envía un evento ORDER_UPDATED a todos los clientes suscriptos. */
  public void publishOrderUpdated(OrderResponse order) {
    if (order == null || order.id() == null) {
      log.warn("Intento de publicar ORDER_UPDATED con order nulo o sin id");
      return;
    }

    OrderEventPayload event = new OrderEventPayload("ORDER_UPDATED", order);

    log.debug(
        "Publishing ORDER_UPDATED event for orderId={} to topic={}", order.id(), ORDERS_TOPIC);

    messagingTemplate.convertAndSend(ORDERS_TOPIC, event);
  }
}
