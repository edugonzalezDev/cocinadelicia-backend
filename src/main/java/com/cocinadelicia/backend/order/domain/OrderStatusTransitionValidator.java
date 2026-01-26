// src/main/java/com/cocinadelicia/backend/order/domain/OrderStatusTransitionValidator.java
package com.cocinadelicia.backend.order.domain;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class OrderStatusTransitionValidator {

  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED =
      new EnumMap<>(OrderStatus.class);

  static {
    // MVP del sprint (sin CONFIRMED y sin OUT_FOR_DELIVERY en UI, pero dejamos habilitado hacia
    // DELIVERED)
    ALLOWED.put(OrderStatus.CREATED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.READY, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.OUT_FOR_DELIVERY));
    // Estados terminales o que no usamos en este sprint
    ALLOWED.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
    ALLOWED.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    ALLOWED.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED));
    ALLOWED.put(OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED));
  }

  private OrderStatusTransitionValidator() {}

  public static void validateOrThrow(OrderStatus current, OrderStatus next) {
    var allowed = ALLOWED.getOrDefault(current, Set.of());
    if (!allowed.contains(next)) {
      // Code estandarizado para front/toasts y tests
      throw new BadRequestException(
          "INVALID_STATUS_TRANSITION", "No se puede pasar de " + current + " a " + next);
    }
  }
}
