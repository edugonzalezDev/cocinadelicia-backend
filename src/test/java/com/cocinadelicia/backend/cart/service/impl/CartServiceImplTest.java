package com.cocinadelicia.backend.cart.service.impl;

import com.cocinadelicia.backend.cart.dto.*;
import com.cocinadelicia.backend.cart.mapper.CartMapper;
import com.cocinadelicia.backend.cart.model.Cart;
import com.cocinadelicia.backend.cart.model.CartItem;
import com.cocinadelicia.backend.cart.repository.CartItemRepository;
import com.cocinadelicia.backend.cart.repository.CartRepository;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CartServiceImpl.
 * Sprint S07 - US01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartServiceImpl Unit Tests")
class CartServiceImplTest {

  @Mock private CartRepository cartRepository;
  @Mock private CartItemRepository cartItemRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProductRepository productRepository;
  @Mock private ProductVariantRepository productVariantRepository;
  @Mock private CartMapper cartMapper;

  @InjectMocks private CartServiceImpl cartService;

  private AppUser testUser;
  private Cart testCart;
  private Product testProduct;
  private ProductVariant testVariant;
  private CartResponse mockCartResponse;

  @BeforeEach
  void setUp() {
    testUser = AppUser.builder().id(1L).email("test@example.com").build();

    testCart = Cart.builder().id(1L).user(testUser).items(new ArrayList<>()).build();

    testProduct =
        Product.builder()
            .id(10L)
            .name("Test Product")
            .slug("test-product")
            .isActive(true)
            .build();

    testVariant =
        ProductVariant.builder()
            .id(20L)
            .product(testProduct)
            .name("Test Variant")
            .isActive(true)
            .build();

    mockCartResponse =
        new CartResponse(
            1L,
            1L,
            List.of(),
            0,
            CurrencyCode.UYU,
            BigDecimal.ZERO,
            Instant.now(),
            Instant.now());
  }

  // ========== Tests de getOrCreateCart ==========

  @Test
  @DisplayName("Should return existing cart when user has cart")
  void shouldReturnExistingCartWhenUserHasCart() {
    // Given
    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(cartMapper.toResponse(testCart)).thenReturn(mockCartResponse);

    // When
    CartResponse result = cartService.getOrCreateCart(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.cartId()).isEqualTo(1L);
    verify(cartRepository, never()).save(any(Cart.class));
  }

  @Test
  @DisplayName("Should create new cart when user has no cart")
  void shouldCreateNewCartWhenUserHasNoCart() {
    // Given
    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
    when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

    // When
    CartResponse result = cartService.getOrCreateCart(1L);

    // Then
    assertThat(result).isNotNull();
    verify(cartRepository).save(any(Cart.class));
  }

  @Test
  @DisplayName("Should throw exception when user not found creating cart")
  void shouldThrowExceptionWhenUserNotFoundCreatingCart() {
    // Given
    when(cartRepository.findByUser_IdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cartService.getOrCreateCart(999L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("USER_NOT_FOUND");
  }

  // ========== Tests de addItem ==========

  @Test
  @DisplayName("Should add new item to cart successfully")
  void shouldAddNewItemToCartSuccessfully() {
    // Given
    AddToCartRequest request = new AddToCartRequest(10L, 20L, 2, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(20L)).thenReturn(Optional.of(testVariant));
    when(cartItemRepository.findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
            anyLong(), anyLong(), anyString()))
        .thenReturn(Optional.empty());
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
    when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

    // When
    CartResponse result = cartService.addItem(1L, request);

    // Then
    assertThat(result).isNotNull();
    // El nuevo item se persiste automáticamente vía cascade de cartRepository.save()
    verify(cartRepository).save(testCart);
    verify(cartItemRepository, never()).save(any(CartItem.class)); // No save explícito para items nuevos
  }

  @Test
  @DisplayName("Should increment quantity when adding same item")
  void shouldIncrementQuantityWhenAddingSameItem() {
    // Given
    CartItem existingItem =
        CartItem.builder()
            .id(1L)
            .cart(testCart)
            .product(testProduct)
            .productVariant(testVariant)
            .quantity(2)
            .modifiersHash("NONE")
            .build();

    AddToCartRequest request = new AddToCartRequest(10L, 20L, 3, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(20L)).thenReturn(Optional.of(testVariant));
    when(cartItemRepository.findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
            anyLong(), anyLong(), eq("NONE")))
        .thenReturn(Optional.of(existingItem));
    when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
    when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

    // When
    cartService.addItem(1L, request);

    // Then
    assertThat(existingItem.getQuantity()).isEqualTo(5); // 2 + 3
    verify(cartItemRepository).save(existingItem);
  }

  @Test
  @DisplayName("Should throw exception when product not found")
  void shouldThrowExceptionWhenProductNotFound() {
    // Given
    AddToCartRequest request = new AddToCartRequest(999L, 20L, 1, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("PRODUCT_NOT_FOUND");
  }

  @Test
  @DisplayName("Should throw exception when variant not found")
  void shouldThrowExceptionWhenVariantNotFound() {
    // Given
    AddToCartRequest request = new AddToCartRequest(10L, 999L, 1, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("VARIANT_NOT_FOUND");
  }

  @Test
  @DisplayName("Should throw exception when variant does not belong to product")
  void shouldThrowExceptionWhenVariantDoesNotBelongToProduct() {
    // Given
    Product otherProduct = Product.builder().id(99L).name("Other Product").build();
    ProductVariant otherVariant =
        ProductVariant.builder()
            .id(20L)
            .product(otherProduct)
            .name("Other Variant")
            .isActive(true)
            .build();

    AddToCartRequest request = new AddToCartRequest(10L, 20L, 1, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(20L)).thenReturn(Optional.of(otherVariant));

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("VARIANT_MISMATCH");
  }

  @Test
  @DisplayName("Should throw exception when variant is inactive")
  void shouldThrowExceptionWhenVariantIsInactive() {
    // Given
    testVariant.setActive(false);
    AddToCartRequest request = new AddToCartRequest(10L, 20L, 1, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(20L)).thenReturn(Optional.of(testVariant));

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("VARIANT_INACTIVE");
  }

  @Test
  @DisplayName("Should throw exception when cart is full (50 items)")
  void shouldThrowExceptionWhenCartIsFull() {
    // Given
    List<CartItem> items = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      items.add(CartItem.builder().id((long) i).build());
    }
    testCart.getItems().addAll(items);

    AddToCartRequest request = new AddToCartRequest(10L, 20L, 1, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("CART_FULL");
  }

  @Test
  @DisplayName("Should throw exception when quantity would exceed 99")
  void shouldThrowExceptionWhenQuantityWouldExceed99() {
    // Given
    CartItem existingItem =
        CartItem.builder()
            .id(1L)
            .cart(testCart)
            .product(testProduct)
            .productVariant(testVariant)
            .quantity(95)
            .modifiersHash("NONE")
            .build();

    AddToCartRequest request = new AddToCartRequest(10L, 20L, 5, null);

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
    when(productVariantRepository.findById(20L)).thenReturn(Optional.of(testVariant));
    when(cartItemRepository.findByCart_IdAndProductVariant_IdAndModifiersHashAndDeletedAtIsNull(
            anyLong(), anyLong(), eq("NONE")))
        .thenReturn(Optional.of(existingItem));

    // When & Then
    assertThatThrownBy(() -> cartService.addItem(1L, request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("QUANTITY_EXCEEDED");
  }

  // ========== Tests de updateItemQuantity ==========

  @Test
  @DisplayName("Should update item quantity successfully")
  void shouldUpdateItemQuantitySuccessfully() {
    // Given
    CartItem item = CartItem.builder().id(1L).cart(testCart).quantity(2).build();

    UpdateCartItemRequest request = new UpdateCartItemRequest(5);

    when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
    when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

    // When
    CartResponse result = cartService.updateItemQuantity(1L, 1L, request);

    // Then
    assertThat(result).isNotNull();
    assertThat(item.getQuantity()).isEqualTo(5);
    verify(cartItemRepository).save(item);
  }

  @Test
  @DisplayName("Should throw exception when item not found")
  void shouldThrowExceptionWhenItemNotFound() {
    // Given
    UpdateCartItemRequest request = new UpdateCartItemRequest(5);

    when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cartService.updateItemQuantity(1L, 999L, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("ITEM_NOT_FOUND");
  }

  @Test
  @DisplayName("Should throw exception when updating item from another user")
  void shouldThrowExceptionWhenUpdatingItemFromAnotherUser() {
    // Given
    AppUser otherUser = AppUser.builder().id(2L).build();
    Cart otherCart = Cart.builder().id(2L).user(otherUser).build();
    CartItem item = CartItem.builder().id(1L).cart(otherCart).build();

    UpdateCartItemRequest request = new UpdateCartItemRequest(5);

    when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

    // When & Then
    assertThatThrownBy(() -> cartService.updateItemQuantity(1L, 1L, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("CART_NOT_FOUND");
  }

  // ========== Tests de removeItem ==========

  @Test
  @DisplayName("Should remove item successfully")
  void shouldRemoveItemSuccessfully() {
    // Given
    CartItem item = CartItem.builder().id(1L).cart(testCart).build();

    when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
    when(cartMapper.toResponse(any(Cart.class))).thenReturn(mockCartResponse);

    // When
    CartResponse result = cartService.removeItem(1L, 1L);

    // Then
    assertThat(result).isNotNull();
    verify(cartItemRepository).delete(item);
    verify(cartRepository).save(testCart);
  }

  @Test
  @DisplayName("Should throw exception when removing item from another user")
  void shouldThrowExceptionWhenRemovingItemFromAnotherUser() {
    // Given
    AppUser otherUser = AppUser.builder().id(2L).build();
    Cart otherCart = Cart.builder().id(2L).user(otherUser).build();
    CartItem item = CartItem.builder().id(1L).cart(otherCart).build();

    when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

    // When & Then
    assertThatThrownBy(() -> cartService.removeItem(1L, 1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("CART_NOT_FOUND");
  }

  // ========== Tests de clearCart ==========

  @Test
  @DisplayName("Should clear cart successfully")
  void shouldClearCartSuccessfully() {
    // Given
    CartItem item1 = CartItem.builder().id(1L).build();
    CartItem item2 = CartItem.builder().id(2L).build();
    testCart.getItems().addAll(List.of(item1, item2));

    when(cartRepository.findByUser_IdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

    // When
    cartService.clearCart(1L);

    // Then
    assertThat(testCart.getItems()).isEmpty();
    verify(cartRepository).save(testCart);
  }

  @Test
  @DisplayName("Should throw exception when cart not found on clear")
  void shouldThrowExceptionWhenCartNotFoundOnClear() {
    // Given
    when(cartRepository.findByUser_IdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> cartService.clearCart(999L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("CART_NOT_FOUND");
  }
}
