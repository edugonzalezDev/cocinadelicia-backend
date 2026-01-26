package com.cocinadelicia.backend.order.admin.service;

import com.cocinadelicia.backend.order.admin.dto.CreateOrderAdminRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderFilterRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderStatsResponse;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderAdminService {

  /**
   * Crea una orden en nombre de un cliente (Admin).
   */
  OrderAdminResponse createOrderForUser(CreateOrderAdminRequest request);

  /**
   * Obtiene todas las órdenes con filtros avanzados.
   */
  Page<OrderAdminResponse> getAllOrders(OrderFilterRequest filters, Pageable pageable);

  /**
   * Obtiene detalle completo de una orden (incluye datos sensibles).
   */
  OrderAdminResponse getOrderById(Long id);

  /**
   * Actualiza el estado de una orden (Admin puede hacer cualquier transición válida).
   */
  OrderAdminResponse updateStatus(Long orderId, OrderStatus newStatus, String reason);

  /**
   * Asigna un chef a una orden.
   */
  OrderAdminResponse assignChef(Long orderId, String chefEmail);

  /**
   * Elimina una orden (soft delete).
   */
  void deleteOrder(Long orderId);

  /**
   * Obtiene estadísticas de órdenes para dashboard.
   */
  OrderStatsResponse getStats();
}
