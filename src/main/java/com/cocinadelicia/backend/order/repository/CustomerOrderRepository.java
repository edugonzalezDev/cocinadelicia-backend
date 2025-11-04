package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.CustomerOrder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

  // NUEVO: libre para que el sort lo defina el Pageable del controller
  Page<CustomerOrder> findByUser_Id(Long userId, Pageable pageable);

  // Control de pertenencia (detalle)
  Optional<CustomerOrder> findByIdAndUser_Id(Long orderId, Long userId);

  // Detalle "cargado" (evita N+1) - items + payments (lo podés usar en getOrderById si querés)
  @Query(
      """
    select o
    from CustomerOrder o
    left join fetch o.items i
    left join fetch o.payments p
    where o.id = :orderId and o.user.id = :userId
  """)
  Optional<CustomerOrder> findDetailedByIdAndUserId(
      @Param("orderId") Long orderId, @Param("userId") Long userId);
}
