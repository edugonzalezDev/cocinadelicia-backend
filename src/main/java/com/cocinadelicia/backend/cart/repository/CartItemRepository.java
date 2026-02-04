package com.cocinadelicia.backend.cart.repository;

import com.cocinadelicia.backend.cart.model.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para CartItem.
 * Sprint S07 - US01
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

  /**
   * Busca items de un carrito específico.
   *
   * @param cartId ID del carrito
   * @return Lista de items activos
   */
  List<CartItem> findByCart_IdAndDeletedAtIsNull(Long cartId);

  /**
   * Busca un item específico por variante y hash de modifiers en un carrito.
   *
   * @param cartId ID del carrito
   * @param variantId ID de la variante
   * @param modifiersHash Hash de modifiers
   * @return Optional con el item si existe
   */
  Optional<CartItem>
      findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
          Long cartId, Long variantId, String modifiersHash);
}
