package com.cocinadelicia.backend.cart.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.user.model.AppUser;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad Cart: carrito de compras persistente por usuario autenticado.
 * Sprint S07 - US01
 */
@Entity
@Table(
    name = "cart",
    indexes = {
      @Index(name = "idx_cart_user", columnList = "user_id"),
      @Index(name = "idx_cart_active", columnList = "user_id, deleted_at")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE cart SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Cart extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CartItem> items = new ArrayList<>();

  // Helper methods
  public void addItem(CartItem item) {
    items.add(item);
    item.setCart(this);
  }

  public void removeItem(CartItem item) {
    items.remove(item);
    item.setCart(null);
  }

  public void clearItems() {
    items.clear();
  }
}
