package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentRepository extends JpaRepository<Payment, Long> {}
