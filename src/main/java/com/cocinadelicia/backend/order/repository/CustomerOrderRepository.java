package com.cocinadelicia.backend.order.repository;

import com.cocinadelicia.backend.order.model.CustomerOrder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository
    extends JpaRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder> {

  Page<CustomerOrder> findByUser_Id(Long userId, Pageable pageable);

  Optional<CustomerOrder> findByIdAndUser_Id(Long orderId, Long userId);

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
