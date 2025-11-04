package com.cocinadelicia.backend.order.service;

import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

  /**
   * Crea un pedido para un usuario (id de app_user) a partir del payload. Debe: validar, resolver
   * precios, calcular totales, persistir y devolver OrderResponse.
   */
  OrderResponse createOrder(CreateOrderRequest request, Long appUserId);

  /** Obtiene un pedido por id, validando que pertenezca al usuario si corresponde a "mine". */
  OrderResponse getOrderById(Long orderId, Long appUserId);

  /** Lista los pedidos del usuario autenticado, ordenados desc por created_at. */
  Page<OrderResponse> getMyOrders(Long appUserId, Pageable pageable);
}
