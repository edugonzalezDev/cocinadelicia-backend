package com.cocinadelicia.backend.cart.controller;

import com.cocinadelicia.backend.cart.dto.AddToCartRequest;
import com.cocinadelicia.backend.cart.dto.CartResponse;
import com.cocinadelicia.backend.cart.dto.UpdateCartItemRequest;
import com.cocinadelicia.backend.cart.service.CartService;
import com.cocinadelicia.backend.common.web.ApiError;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de carrito de compras.
 * Sprint S07 - US01
 */
@Log4j2
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "cart", description = "Operaciones de carrito de compras del usuario autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class CartController {

  private final CartService cartService;
  private final CurrentUserService currentUserService;

  @Operation(
      summary = "Obtener carrito del usuario",
      description =
          """
      Obtiene el carrito del usuario autenticado con todos sus items y precios actuales.
      Si el usuario no tiene carrito, se crea uno nuevo vacío.
      Requiere token JWT válido.
      """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Carrito obtenido correctamente",
            content = @Content(schema = @Schema(implementation = CartResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @GetMapping
  public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal Jwt jwt) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug("GET /api/cart userId={}", userId);
    return ResponseEntity.ok(cartService.getOrCreateCart(userId));
  }

  @Operation(
      summary = "Agregar item al carrito",
      description =
          """
      Agrega un item al carrito del usuario autenticado.
      Si ya existe un item con la misma variante y modifiers, incrementa la cantidad.
      Requiere token JWT válido.
      """,
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Item agregado correctamente",
            content = @Content(schema = @Schema(implementation = CartResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request inválido (validación de negocio o @Valid)",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Producto o variante no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @PostMapping("/items")
  public ResponseEntity<CartResponse> addItem(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddToCartRequest request) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug(
        "POST /api/cart/items userId={} productId={} variantId={} quantity={}",
        userId,
        request.productId(),
        request.productVariantId(),
        request.quantity());
    CartResponse response = cartService.addItem(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Actualizar cantidad de un item",
      description =
          """
      Actualiza la cantidad de un item existente en el carrito.
      Solo el propietario del carrito puede actualizar sus items.
      Requiere token JWT válido.
      """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Cantidad actualizada correctamente",
            content = @Content(schema = @Schema(implementation = CartResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request inválido (cantidad fuera de rango)",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Item no encontrado o no pertenece al usuario",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @PutMapping("/items/{itemId}")
  public ResponseEntity<CartResponse> updateItemQuantity(
      @AuthenticationPrincipal Jwt jwt,
      @Parameter(description = "ID del item a actualizar", example = "1") @PathVariable Long itemId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug(
        "PUT /api/cart/items/{} userId={} newQuantity={}", itemId, userId, request.quantity());
    return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, request));
  }

  @Operation(
      summary = "Eliminar item del carrito",
      description =
          """
      Elimina un item del carrito del usuario autenticado.
      Solo el propietario del carrito puede eliminar sus items.
      Requiere token JWT válido.
      """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Item eliminado correctamente",
            content = @Content(schema = @Schema(implementation = CartResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Item no encontrado o no pertenece al usuario",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<CartResponse> removeItem(
      @AuthenticationPrincipal Jwt jwt,
      @Parameter(description = "ID del item a eliminar", example = "1") @PathVariable Long itemId) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug("DELETE /api/cart/items/{} userId={}", itemId, userId);
    return ResponseEntity.ok(cartService.removeItem(userId, itemId));
  }

  @Operation(
      summary = "Vaciar todos los items del carrito",
      description =
          """
      Elimina todos los items del carrito del usuario autenticado.
      El carrito en sí permanece, pero queda vacío.
      Requiere token JWT válido.
      Este endpoint es equivalente a DELETE /api/cart.
      """,
      responses = {
        @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente"),
        @ApiResponse(
            responseCode = "404",
            description = "Carrito no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @DeleteMapping("/items")
  public ResponseEntity<Void> clearAllItems(@AuthenticationPrincipal Jwt jwt) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug("DELETE /api/cart/items userId={}", userId);
    cartService.clearCart(userId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Vaciar carrito completo",
      description =
          """
      Elimina todos los items del carrito del usuario autenticado.
      El carrito en sí permanece, pero queda vacío.
      Requiere token JWT válido.
      Este endpoint es equivalente a DELETE /api/cart/items.
      """,
      responses = {
        @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente"),
        @ApiResponse(
            responseCode = "404",
            description = "Carrito no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @DeleteMapping
  public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Jwt jwt) {
    Long userId = currentUserService.getOrCreateCurrentUserId();
    log.debug("DELETE /api/cart userId={}", userId);
    cartService.clearCart(userId);
    return ResponseEntity.noContent().build();
  }
}
