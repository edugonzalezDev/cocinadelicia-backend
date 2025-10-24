package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
