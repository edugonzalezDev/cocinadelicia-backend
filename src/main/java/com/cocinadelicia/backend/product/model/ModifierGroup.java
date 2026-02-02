package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "modifier_group",
    indexes = {
      @Index(name = "ix_modifier_group_variant", columnList = "product_variant_id"),
      @Index(
          name = "ix_modifier_group_active",
          columnList = "product_variant_id, is_active, sort_order")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE modifier_group SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ModifierGroup extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_variant_id", nullable = false)
  private ProductVariant productVariant;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(name = "min_select", nullable = false)
  @Builder.Default
  private int minSelect = 0;

  @Column(name = "max_select", nullable = false)
  @Builder.Default
  private int maxSelect = 1;

  @Enumerated(EnumType.STRING)
  @Column(name = "selection_mode", length = 20, nullable = false)
  @Builder.Default
  private ModifierSelectionMode selectionMode = ModifierSelectionMode.SINGLE;

  @Column(name = "required_total_qty")
  private Integer requiredTotalQty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "default_option_id")
  private ModifierOption defaultOption;

  @Column(name = "sort_order", nullable = false)
  @Builder.Default
  private int sortOrder = 0;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean active = true;

  @OneToMany(mappedBy = "modifierGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC, id ASC")
  @BatchSize(size = 50)
  @Builder.Default
  private List<ModifierOption> options = new ArrayList<>();
}
