package com.cocinadelicia.backend.order.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "order_item", indexes = @Index(name = "ix_order_item_order", columnList = "order_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE order_item SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrderItem extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private CustomerOrder order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_variant_id", nullable = false)
  private ProductVariant productVariant;

  @Column(name = "product_name", length = 191, nullable = false)
  private String productName;

  @Column(name = "variant_name", length = 191, nullable = false)
  private String variantName;

  @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private Integer quantity = 1;

  @Column(name = "line_total", precision = 10, scale = 2, nullable = false)
  private BigDecimal lineTotal;

  @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItemModifier> modifiers = new ArrayList<>();
}
