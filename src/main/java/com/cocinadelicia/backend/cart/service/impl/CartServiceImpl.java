package com.cocinadelicia.backend.cart.service.impl;

import com.cocinadelicia.backend.cart.dto.AddToCartRequest;
import com.cocinadelicia.backend.cart.dto.CartResponse;
import com.cocinadelicia.backend.cart.dto.UpdateCartItemRequest;
import com.cocinadelicia.backend.cart.mapper.CartMapper;
import com.cocinadelicia.backend.cart.model.Cart;
import com.cocinadelicia.backend.cart.model.CartItem;
import com.cocinadelicia.backend.cart.repository.CartItemRepository;
import com.cocinadelicia.backend.cart.repository.CartRepository;
import com.cocinadelicia.backend.cart.service.CartService;
import com.cocinadelicia.backend.cart.util.ModifiersHashUtil;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de carrito.
 * Sprint S07 - US01
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;
  private final CartMapper cartMapper;

  @PersistenceContext private EntityManager entityManager;

  private static final int MAX_ITEMS_PER_CART = 50;

  @Override
  @Transactional(readOnly = true)
  public CartResponse getOrCreateCart(Long userId) {
    Cart cart =
        cartRepository
            .findByUser_IdAndDeletedAtIsNull(userId)
            .orElseGet(() -> createNewCart(userId));

    log.info("Cart.get userId={} cartId={} itemCount={}", userId, cart.getId(), cart.getItems().size());
    return cartMapper.toResponse(cart);
  }

  @Override
  public CartResponse addItem(Long userId, AddToCartRequest request) {
    // 1️⃣ Validaciones básicas
    validateAddToCartRequest(request);

    // 2️⃣ Obtener o crear carrito
    Cart cart =
        cartRepository
            .findByUser_IdAndDeletedAtIsNull(userId)
            .orElseGet(() -> createNewCart(userId));

    // 3️⃣ Validar límite de items
    if (cart.getItems().size() >= MAX_ITEMS_PER_CART) {
      throw new BadRequestException(
          "CART_FULL",
          "El carrito ha alcanzado el límite máximo de " + MAX_ITEMS_PER_CART + " items");
    }

    // 4️⃣ Validar producto y variante
    Product product =
        productRepository
            .findById(request.productId())
            .orElseThrow(
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado"));

    ProductVariant variant =
        productVariantRepository
            .findById(request.productVariantId())
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada"));

    // Validar que la variante pertenece al producto
    if (!variant.getProduct().getId().equals(product.getId())) {
      throw new BadRequestException(
          "VARIANT_MISMATCH", "La variante no pertenece al producto indicado");
    }

    // Validar que la variante está activa
    if (!variant.isActive()) {
      throw new BadRequestException("VARIANT_INACTIVE", "La variante seleccionada no está disponible");
    }

    // 5️⃣ Generar hash de modifiers
    String modifiersHash = ModifiersHashUtil.generateHash(request.modifiers());
    String modifiersJson = ModifiersHashUtil.toJson(request.modifiers());

    // 6️⃣ Intentar agregar/actualizar con manejo de race condition
    try {
      return addOrUpdateCartItem(cart, product, variant, request, modifiersHash, modifiersJson, userId);
    } catch (DataIntegrityViolationException e) {
      // Race condition detectado: el item fue creado por otra transacción
      log.warn(
          "Cart.addItem.raceCondition detected userId={} cartId={} variantId={} - retrying",
          userId,
          cart.getId(),
          variant.getId());

      entityManager.clear(); // Limpiar cache de Hibernate

      // Buscar nuevamente el item que debe existir ahora
      var existingItem =
          cartItemRepository.findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
              cart.getId(), variant.getId(), modifiersHash);

      if (existingItem.isPresent()) {
        CartItem item = existingItem.get();
        int newQuantity = item.getQuantity() + request.quantity();

        if (newQuantity > 99) {
          throw new BadRequestException(
              "QUANTITY_EXCEEDED", "La cantidad total no puede exceder 99 unidades");
        }

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        entityManager.flush();

        log.info(
            "Cart.addItem.increment.retry userId={} cartId={} itemId={} variantId={} newQty={}",
            userId,
            cart.getId(),
            item.getId(),
            variant.getId(),
            newQuantity);

        Cart refreshedCart = cartRepository.findById(cart.getId()).orElse(cart);
        return cartMapper.toResponse(refreshedCart);
      }

      // Si aún no existe, relanzar la excepción original
      throw e;
    }
  }

  /**
   * Méto.do auxiliar para agregar o actualizar un item del carrito.
   * Separado para permitir manejo de race conditions.
   */
  private CartResponse addOrUpdateCartItem(
      Cart cart,
      Product product,
      ProductVariant variant,
      AddToCartRequest request,
      String modifiersHash,
      String modifiersJson,
      Long userId) {

    // Verificar si ya existe el mismo item
    var existingItem =
        cartItemRepository.findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
            cart.getId(), variant.getId(), modifiersHash);

    if (existingItem.isPresent()) {
      // Item ya existe → incrementar cantidad
      CartItem item = existingItem.get();
      int newQuantity = item.getQuantity() + request.quantity();

      if (newQuantity > 99) {
        throw new BadRequestException(
            "QUANTITY_EXCEEDED", "La cantidad total no puede exceder 99 unidades");
      }

      item.setQuantity(newQuantity);
      cartItemRepository.save(item);
      entityManager.flush(); // ✅ Flush explícito para asegurar persistencia inmediata

      log.info(
          "Cart.addItem.increment userId={} cartId={} itemId={} productId={} variantId={} oldQty={} newQty={}",
          userId,
          cart.getId(),
          item.getId(),
          product.getId(),
          variant.getId(),
          item.getQuantity() - request.quantity(),
          newQuantity);

      return cartMapper.toResponse(cart);

    } else {
      // Item nuevo → crear
      CartItem newItem =
          CartItem.builder()
              .cart(cart)
              .product(product)
              .productVariant(variant)
              .productName(product.getName())
              .variantName(variant.getName())
              .quantity(request.quantity())
              .modifiersJson(modifiersJson)
              .modifiersHash(modifiersHash)
              .build();

      cart.addItem(newItem);

      log.info(
          "Cart.addItem.new userId={} cartId={} productId={} variantId={} quantity={}",
          userId,
          cart.getId(),
          product.getId(),
          variant.getId(),
          request.quantity());

      // Guardar carrito y hacer flush para validar constraint inmediatamente
      Cart savedCart = cartRepository.save(cart);
      entityManager.flush(); // ✅ Flush para detectar constraint violation inmediatamente

      return cartMapper.toResponse(savedCart);
    }
  }

  @Override
  public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
    // 1️⃣ Obtener el item y validar ownership
    CartItem item =
        cartItemRepository
            .findById(itemId)
            .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Item no encontrado en el carrito"));

    Cart cart = item.getCart();
    validateCartOwnership(cart, userId);

    // 2️⃣ Actualizar cantidad
    int oldQuantity = item.getQuantity();
    item.setQuantity(request.quantity());
    cartItemRepository.save(item);

    log.info(
        "Cart.updateItem userId={} cartId={} itemId={} oldQty={} newQty={}",
        userId,
        cart.getId(),
        itemId,
        oldQuantity,
        request.quantity());

    // 3️⃣ Devolver carrito actualizado
    return cartMapper.toResponse(cart);
  }

  @Override
  public CartResponse removeItem(Long userId, Long itemId) {
    // 1️⃣ Obtener el item y validar ownership
    CartItem item =
        cartItemRepository
            .findById(itemId)
            .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Item no encontrado en el carrito"));

    Cart cart = item.getCart();
    validateCartOwnership(cart, userId);

    // 2️⃣ Eliminar item (soft delete)
    cart.removeItem(item);
    cartItemRepository.delete(item);

    log.info(
        "Cart.removeItem userId={} cartId={} itemId={} productId={}",
        userId,
        cart.getId(),
        itemId,
        item.getProduct().getId());

    // 3️⃣ Devolver carrito actualizado
    Cart savedCart = cartRepository.save(cart);
    return cartMapper.toResponse(savedCart);
  }

  @Override
  public void clearCart(Long userId) {
    // 1️⃣ Obtener carrito
    Cart cart =
        cartRepository
            .findByUser_IdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new NotFoundException("CART_NOT_FOUND", "Carrito no encontrado"));

    // 2️⃣ Eliminar todos los items
    int itemCount = cart.getItems().size();
    cart.clearItems();
    cartRepository.save(cart);

    log.info("Cart.clear userId={} cartId={} itemsRemoved={}", userId, cart.getId(), itemCount);
  }

  // ========== Métodos privados auxiliares ==========

  /**
   * Crea un nuevo carrito para el usuario.
   */
  private Cart createNewCart(Long userId) {
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado"));

    Cart newCart = Cart.builder().user(user).build();
    Cart saved = cartRepository.save(newCart);

    log.info("Cart.create userId={} cartId={}", userId, saved.getId());
    return saved;
  }

  /**
   * Valida que el carrito pertenece al usuario.
   */
  private void validateCartOwnership(Cart cart, Long userId) {
    if (!cart.getUser().getId().equals(userId)) {
      throw new NotFoundException("CART_NOT_FOUND", "Carrito no encontrado");
    }
  }

  /**
   * Valida el request de agregar al carrito.
   */
  private void validateAddToCartRequest(AddToCartRequest request) {
    if (request.quantity() < 1 || request.quantity() > 99) {
      throw new BadRequestException(
          "INVALID_QUANTITY", "La cantidad debe estar entre 1 y 99");
    }
  }
}
