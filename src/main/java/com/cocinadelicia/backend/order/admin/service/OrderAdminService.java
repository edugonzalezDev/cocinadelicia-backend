package com.cocinadelicia.backend.order.admin.service;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.admin.dto.CreateOrderAdminRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminCustomerResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminDetailsResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderFilterRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderStatsResponse;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderCustomerRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderDetailsRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderItemsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderAdminService {

  /** Crea una orden en nombre de un cliente (Admin). */
  OrderAdminResponse createOrderForUser(CreateOrderAdminRequest request);

  /** Obtiene todas las órdenes con filtros avanzados. */
  Page<OrderAdminResponse> getAllOrders(OrderFilterRequest filters, Pageable pageable);

  /** Obtiene detalle completo de una orden (incluye datos sensibles). */
  OrderAdminResponse getOrderById(Long id);

  /** Obtiene detalle para el modal de admin. */
  OrderAdminDetailsResponse getOrderDetails(Long id);

  /** Actualiza el estado de una orden (Admin puede hacer cualquier transición válida). */
  OrderAdminResponse updateStatus(Long orderId, OrderStatus newStatus, String reason);

  /** Asigna un chef a una orden. */
  OrderAdminResponse assignChef(Long orderId, String chefEmail);

  /** Elimina una orden (soft delete). */
  void deleteOrder(Long orderId);

  /** Obtiene datos del cliente asociados a una orden. */
  OrderAdminCustomerResponse getOrderCustomer(Long orderId);

  /** Obtiene estadísticas de órdenes para dashboard. */
  OrderStatsResponse getStats();

  /** Reemplaza los ítems de la orden y recalcula totales (solo estados editables). */
  OrderAdminResponse updateItems(Long orderId, UpdateOrderItemsRequest request);

  /** Actualiza notas, fulfillment, envío y requestedAt de la orden. */
  OrderAdminResponse updateDetails(Long orderId, UpdateOrderDetailsRequest request);

  /** Cambia el cliente asociado a la orden. */
  OrderAdminResponse updateCustomer(Long orderId, UpdateOrderCustomerRequest request);
}
