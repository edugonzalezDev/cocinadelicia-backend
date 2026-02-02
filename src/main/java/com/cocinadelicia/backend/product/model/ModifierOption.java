package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "modifier_option",
    indexes = {@Index(name = "ix_modifier_option_group", columnList = "group_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@BatchSize(size = 50)
@SQLDelete(sql = "UPDATE modifier_option SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ModifierOption extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private ModifierGroup modifierGroup;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(name = "sort_order", nullable = false)
  @Builder.Default
  private int sortOrder = 0;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean active = true;

  @Column(name = "price_delta", precision = 10, scale = 2)
  private BigDecimal priceDelta;

  @Column(name = "is_exclusive", nullable = false)
  @Builder.Default
  private boolean exclusive = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "linked_product_variant_id")
  private ProductVariant linkedProductVariant;
}
