package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.OrderItemModifier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemModifierRepository extends JpaRepository<OrderItemModifier, Long> {
  List<OrderItemModifier> findByOrderItem_Id(Long orderItemId);
}
