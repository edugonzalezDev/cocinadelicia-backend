package com.cocinadelicia.backend.cart.service;

import com.cocinadelicia.backend.cart.dto.AddToCartRequest;
import com.cocinadelicia.backend.cart.dto.CartResponse;
import com.cocinadelicia.backend.cart.dto.UpdateCartItemRequest;

/**
 * Servicio para gestionar carritos de compra.
 * Sprint S07 - US01
 */
public interface CartService {

  /**
   * Obtiene el carrito del usuario o crea uno nuevo si no existe.
   *
   * @param userId ID del usuario autenticado
   * @return CartResponse con el carrito actual
   */
  CartResponse getOrCreateCart(Long userId);

  /**
   * Agrega un item al carrito. Si ya existe el mismo item (misma variante + modifiers),
   * incrementa la cantidad.
   *
   * @param userId ID del usuario autenticado
   * @param request Datos del item a agregar
   * @return CartResponse actualizado
   */
  CartResponse addItem(Long userId, AddToCartRequest request);

  /**
   * Actualiza la cantidad de un item existente en el carrito.
   *
   * @param userId ID del usuario autenticado
   * @param itemId ID del item a actualizar
   * @param request Nueva cantidad
   * @return CartResponse actualizado
   */
  CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request);

  /**
   * Elimina un item del carrito.
   *
   * @param userId ID del usuario autenticado
   * @param itemId ID del item a eliminar
   * @return CartResponse actualizado
   */
  CartResponse removeItem(Long userId, Long itemId);

  /**
   * Vacía completamente el carrito del usuario.
   *
   * @param userId ID del usuario autenticado
   */
  void clearCart(Long userId);
}
