package com.cocinadelicia.backend.cart.repository;

import com.cocinadelicia.backend.cart.model.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para Cart.
 * Sprint S07 - US01
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  /**
   * Busca el carrito activo de un usuario.
   *
   * @param userId ID del usuario
   * @return Optional con el carrito si existe
   */
  Optional<Cart> findByUser_IdAndDeletedAtIsNull(Long userId);

  /**
   * Verifica si existe un carrito activo para un usuario.
   *
   * @param userId ID del usuario
   * @return true si existe, false si no
   */
  boolean existsByUser_IdAndDeletedAtIsNull(Long userId);
}
