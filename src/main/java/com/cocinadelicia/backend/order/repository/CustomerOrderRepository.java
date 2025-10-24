package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {}
