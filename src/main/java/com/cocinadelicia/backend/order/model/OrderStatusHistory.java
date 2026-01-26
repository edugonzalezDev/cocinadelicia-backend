package com.cocinadelicia.backend.order.model;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(
    name = "order_status_history",
    indexes = {
      @Index(name = "idx_order_status_history_order_id", columnList = "order_id"),
      @Index(name = "idx_order_status_history_changed_at", columnList = "changed_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_status", length = 30)
  private OrderStatus fromStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_status", nullable = false, length = 30)
  private OrderStatus toStatus;

  @Column(name = "changed_by", nullable = false)
  private String changedBy;

  @Column(name = "changed_at", nullable = false)
  private Instant changedAt;

  @Column(name = "reason", length = 500)
  private String reason;

  @PrePersist
  protected void onCreate() {
    if (changedAt == null) {
      changedAt = Instant.now();
    }
  }
}
