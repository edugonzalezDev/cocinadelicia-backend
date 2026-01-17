package com.cocinadelicia.backend.order.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.user.model.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "customer_order",
    indexes = {
      @Index(name = "ix_order_user_created", columnList = "user_id, created_at"),
      @Index(name = "ix_order_status_created", columnList = "status, created_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE customer_order SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CustomerOrder extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  @Builder.Default
  private OrderStatus status = OrderStatus.CREATED;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private FulfillmentType fulfillment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  @Builder.Default
  private CurrencyCode currency = CurrencyCode.UYU;

  @Column(name = "subtotal_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotalAmount = BigDecimal.ZERO;

  @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal taxAmount = BigDecimal.ZERO;

  @Column(name = "discount_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal discountAmount = BigDecimal.ZERO;

  @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  // Snapshot envío (solo si DELIVERY)
  @Column(name = "ship_name", length = 191)
  private String shipName;

  @Column(name = "ship_phone", length = 50)
  private String shipPhone;

  @Column(name = "ship_line1", length = 191)
  private String shipLine1;

  @Column(name = "ship_line2", length = 191)
  private String shipLine2;

  @Column(name = "ship_city", length = 100)
  private String shipCity;

  @Column(name = "ship_region", length = 100)
  private String shipRegion;

  @Column(name = "ship_postal_code", length = 20)
  private String shipPostalCode;

  @Column(name = "ship_reference", length = 191)
  private String shipReference;

  @Column(name = "notes", columnDefinition = "text")
  private String notes;

  @Column(name = "requested_at")
  private Instant requestedAt;

  @Column(name = "delivered_at")
  private Instant deliveredAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Payment> payments = new ArrayList<>();

  // Helpers
  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public void addPayment(Payment p) {
    payments.add(p);
    p.setOrder(this);
  }
}
