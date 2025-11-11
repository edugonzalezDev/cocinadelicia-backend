// src/main/java/com/cocinadelicia/backend/order/repository/spec/OrderSpecifications.java
package com.cocinadelicia.backend.order.repository.spec;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {

  public static Specification<CustomerOrder> statusIn(List<OrderStatus> statuses) {
    if (statuses == null || statuses.isEmpty()) return null;
    return (root, q, cb) -> root.get("status").in(statuses);
  }

  public static Specification<CustomerOrder> createdAtGte(Instant fromInclusive) {
    if (fromInclusive == null) return null;
    return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromInclusive);
  }

  public static Specification<CustomerOrder> createdAtLt(Instant toExclusive) {
    if (toExclusive == null) return null;
    return (root, q, cb) -> cb.lessThan(root.get("createdAt"), toExclusive);
  }

  public static Specification<CustomerOrder> userIdEq(Long userId) {
    if (userId == null) return null;
    return (root, q, cb) -> cb.equal(root.get("user").get("id"), userId);
  }
}
