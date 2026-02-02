package com.cocinadelicia.backend.order.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.product.model.ModifierOption;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "order_item_modifier",
    indexes = {
      @Index(name = "ix_order_item_modifier_order", columnList = "order_item_id"),
      @Index(name = "ix_order_item_modifier_option", columnList = "modifier_option_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE order_item_modifier SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrderItemModifier extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_item_id", nullable = false)
  private OrderItem orderItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modifier_option_id", nullable = false)
  private ModifierOption modifierOption;

  @Column(nullable = false)
  @Builder.Default
  private Integer quantity = 1;

  @Column(name = "option_name_snapshot", length = 191, nullable = false)
  private String optionNameSnapshot;

  @Column(name = "price_delta_snapshot", precision = 10, scale = 2)
  private BigDecimal priceDeltaSnapshot;

  @Column(name = "unit_price_snapshot", precision = 10, scale = 2)
  private BigDecimal unitPriceSnapshot;

  @Column(name = "total_price_snapshot", precision = 10, scale = 2)
  private BigDecimal totalPriceSnapshot;

  @Column(name = "linked_product_variant_id_snapshot")
  private Long linkedProductVariantIdSnapshot;
}
