package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

  List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);

  List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);
}
