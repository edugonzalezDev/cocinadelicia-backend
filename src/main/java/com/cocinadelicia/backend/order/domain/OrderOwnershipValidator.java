package com.cocinadelicia.backend.order.domain;

import com.cocinadelicia.backend.common.exception.ForbiddenException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validador de ownership de órdenes. CRÍTICO: Previene que usuarios accedan a órdenes de otros
 * usuarios.
 */
@Component
@RequiredArgsConstructor
public class OrderOwnershipValidator {

  private final CustomerOrderRepository orderRepository;

  /**
   * Valida que el usuario actual es dueño de la orden.
   *
   * @param orderId ID de la orden
   * @param appUserId ID del usuario actual
   * @throws NotFoundException si la orden no existe
   * @throws ForbiddenException si el usuario no es dueño
   */
  public void validateOwnership(Long orderId, Long appUserId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    if (!order.getUser().getId().equals(appUserId)) {
      throw new ForbiddenException("You don't have permission to access this order");
    }
  }

  /**
   * Valida ownership y que la orden no esté eliminada.
   *
   * @param orderId ID de la orden
   * @param appUserId ID del usuario actual
   * @throws NotFoundException si la orden no existe o está eliminada
   * @throws ForbiddenException si el usuario no es dueño
   */
  public void validateOwnershipAndNotDeleted(Long orderId, Long appUserId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    if (order.isDeleted()) {
      throw new NotFoundException("Order not found with id: " + orderId);
    }

    if (!order.getUser().getId().equals(appUserId)) {
      throw new ForbiddenException("You don't have permission to access this order");
    }
  }

  /**
   * Obtiene orden validando ownership.
   *
   * @param orderId ID de la orden
   * @param appUserId ID del usuario actual
   * @return La orden si existe y pertenece al usuario
   * @throws NotFoundException si la orden no existe
   * @throws ForbiddenException si el usuario no es dueño
   */
  public CustomerOrder getOrderWithOwnership(Long orderId, Long appUserId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    if (!order.getUser().getId().equals(appUserId)) {
      throw new ForbiddenException("You don't have permission to access this order");
    }

    return order;
  }

  /**
   * Obtiene orden validando ownership y que no esté eliminada.
   *
   * @param orderId ID de la orden
   * @param appUserId ID del usuario actual
   * @return La orden si existe, pertenece al usuario y no está eliminada
   * @throws NotFoundException si la orden no existe o está eliminada
   * @throws ForbiddenException si el usuario no es dueño
   */
  public CustomerOrder getOrderWithOwnershipAndNotDeleted(Long orderId, Long appUserId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    if (order.isDeleted()) {
      throw new NotFoundException("Order not found with id: " + orderId);
    }

    if (!order.getUser().getId().equals(appUserId)) {
      throw new ForbiddenException("You don't have permission to access this order");
    }

    return order;
  }
}
