// src/main/java/com/cocinadelicia/backend/product/model/ProductVariant.java
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
    name = "product_variant",
    uniqueConstraints = @UniqueConstraint(name = "uk_variant_sku", columnNames = "sku"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_variant SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductVariant extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(length = 191)
  private String sku;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean isActive = true;

  // 👉 Manejo de stock
  @Column(name = "manages_stock", nullable = false)
  @Builder.Default
  private boolean managesStock = false;

  @Column(name = "stock_quantity", nullable = false)
  @Builder.Default
  private int stockQuantity = 0;

  // 👉 NUEVO: flags de visibilidad / marketing
  @Column(name = "is_featured", nullable = false)
  @Builder.Default
  private boolean featured = false;

  @Column(name = "is_daily_menu", nullable = false)
  @Builder.Default
  private boolean dailyMenu = false;

  @Column(name = "is_new", nullable = false)
  @Builder.Default
  private boolean isNew = false;

  @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
  @BatchSize(size = 50)
  @Builder.Default
  private List<PriceHistory> priceHistory = new ArrayList<>();

  @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
  @BatchSize(size = 50)
  @Builder.Default
  private List<ModifierGroup> modifierGroups = new ArrayList<>();

  // Helper opcional para disponibilidad a nivel dominio
  @Transient
  public boolean isAvailable() {
    if (!isActive()) return false;
    if (!managesStock) return true;
    return stockQuantity > 0;
  }
}
