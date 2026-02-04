package com.cocinadelicia.backend.cart.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad CartItem: item individual dentro del carrito.
 * Incluye snapshot de producto/variante/modifiers para mostrar en UI incluso si el producto cambia.
 * Sprint S07 - US01
 */
@Entity
@Table(
    name = "cart_item",
    indexes = {
      @Index(name = "idx_cart_item_cart", columnList = "cart_id"),
      @Index(name = "idx_cart_item_variant", columnList = "product_variant_id"),
      @Index(name = "idx_cart_item_active", columnList = "cart_id, deleted_at")
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_cart_item_unique",
          columnNames = {"cart_id", "product_variant_id", "modifiers_hash"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE cart_item SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class CartItem extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_variant_id", nullable = false)
  private ProductVariant productVariant;

  // Snapshot de nombres para UI
  @Column(name = "product_name", length = 191, nullable = false)
  private String productName;

  @Column(name = "variant_name", length = 191, nullable = false)
  private String variantName;

  // Cantidad
  @Column(name = "quantity", nullable = false)
  private int quantity;

  // Modifiers como JSON
  @Column(name = "modifiers_json", columnDefinition = "TEXT")
  private String modifiersJson;

  // Hash de modifiers para unicidad
  @Column(name = "modifiers_hash", length = 64)
  private String modifiersHash;
}
