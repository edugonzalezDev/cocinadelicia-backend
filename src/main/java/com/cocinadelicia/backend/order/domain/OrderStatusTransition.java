package com.cocinadelicia.backend.order.domain;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

/**
 * Máquina de estados para transiciones de OrderStatus. Define qué transiciones son válidas y para
 * qué roles.
 */
@Log4j2
public class OrderStatusTransition {

  // Transiciones válidas globales
  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          OrderStatus.CREATED, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
          OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
          OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY, OrderStatus.CANCELLED),
          OrderStatus.READY,
              EnumSet.of(
                  OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED, OrderStatus.CANCELLED),
          OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
          OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
          OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));

  // Transiciones permitidas para CLIENTE (solo cancelar)
  private static final Map<OrderStatus, Set<OrderStatus>> CLIENT_TRANSITIONS =
      Map.of(
          OrderStatus.CREATED, EnumSet.of(OrderStatus.CANCELLED),
          OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.CANCELLED));

  // Transiciones permitidas para CHEF (preparación)
  private static final Map<OrderStatus, Set<OrderStatus>> CHEF_TRANSITIONS =
      Map.of(
          OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PREPARING),
          OrderStatus.PREPARING, EnumSet.of(OrderStatus.READY));

  /**
   * Valida que la transición sea permitida globalmente.
   *
   * @throws BadRequestException si la transición no es válida
   */
  public static void validateTransition(OrderStatus from, OrderStatus to) {
    Set<OrderStatus> allowedStates =
        ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(OrderStatus.class));

    if (!allowedStates.contains(to)) {
      throw new BadRequestException(
          String.format(
              "INVALID_STATUS_TRANSITION", "Invalid status transition from %s to %s", from, to));
    }
  }

  /**
   * Valida que un cliente pueda hacer esta transición.
   *
   * @throws BadRequestException si el cliente no puede hacer la transición
   */
  public static void validateClientTransition(OrderStatus from, OrderStatus to) {
    Set<OrderStatus> allowedStates =
        CLIENT_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(OrderStatus.class));

    if (!allowedStates.contains(to)) {
      throw new BadRequestException(
          String.format(
              "FORBIDDEN_TRANSITION", "Client cannot transition order from %s to %s", from, to));
    }
  }

  /**
   * Valida que un chef pueda hacer esta transición.
   *
   * @throws BadRequestException si el chef no puede hacer la transición
   */
  public static void validateChefTransition(OrderStatus from, OrderStatus to) {
    Set<OrderStatus> allowedStates =
        CHEF_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(OrderStatus.class));

    if (!allowedStates.contains(to)) {
      throw new BadRequestException(
          String.format(
              "FORBIDDEN_TRANSITION", "Chef cannot transition order from %s to %s", from, to));
    }
  }

  /** Verifica si el cliente puede cancelar la orden en el estado actual. */
  public static boolean canClientCancel(OrderStatus status) {
    return status == OrderStatus.CREATED || status == OrderStatus.CONFIRMED;
  }

  /** Verifica si el estado es terminal (no permite más transiciones). */
  public static boolean isTerminalStatus(OrderStatus status) {
    return status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED;
  }

  /** Obtiene los estados permitidos desde el estado actual. */
  public static Set<OrderStatus> getAllowedNextStates(OrderStatus current) {
    return ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(OrderStatus.class));
  }
}
