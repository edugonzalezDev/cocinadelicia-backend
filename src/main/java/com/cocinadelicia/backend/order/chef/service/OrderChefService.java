package com.cocinadelicia.backend.order.chef.service;

import com.cocinadelicia.backend.order.chef.dto.OrderChefResponse;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderChefService {

  /**
   * Obtiene órdenes asignadas al chef actual.
   */
  Page<OrderChefResponse> getAssignedOrders(Pageable pageable);

  /**
   * Obtiene solo órdenes activas (CONFIRMED, PREPARING, READY).
   */
  Page<OrderChefResponse> getActiveOrders(Pageable pageable);

  /**
   * Actualiza el estado de una orden (Chef solo puede hacer transiciones permitidas).
   */
  OrderChefResponse updateStatus(Long orderId, OrderStatus newStatus);
}
