// src/main/java/com/cocinadelicia/backend/order/service/OrderService.java
package com.cocinadelicia.backend.order.service;

import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
  OrderResponse createOrder(CreateOrderRequest request, Long appUserId);

  OrderResponse getOrderById(Long orderId, Long appUserId);

  Page<OrderResponse> getAllOrders(Pageable pageable);

  Page<OrderResponse> getMyOrders(Long appUserId, Pageable pageable);

  /** Cambia el estado de un pedido (uso interno admin/chef). */
  OrderResponse updateStatus(Long orderId, String performedBy, UpdateOrderStatusRequest request);
}
