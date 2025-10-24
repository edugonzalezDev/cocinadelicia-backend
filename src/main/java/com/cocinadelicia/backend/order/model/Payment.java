package com.cocinadelicia.backend.order.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.PaymentMethod;
import com.cocinadelicia.backend.common.model.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "payment",
    indexes = {
      @Index(name = "ix_payment_order", columnList = "order_id"),
      @Index(name = "ix_payment_status_created", columnList = "status, created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE payment SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Payment extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private CustomerOrder order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  @Builder.Default
  private PaymentStatus status = PaymentStatus.PENDING;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  @Builder.Default
  private CurrencyCode currency = CurrencyCode.UYU;

  @Column(name = "provider_tx_id", length = 191)
  private String providerTxId;

  @Column(length = 255)
  private String note;
}
