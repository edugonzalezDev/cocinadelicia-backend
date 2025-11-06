package com.cocinadelicia.backend.order.controller;

import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operaciones de pedidos del usuario autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {

  private final OrderService orderService;
  private final UserRepository userRepository;

  @Operation(
      summary = "Crear un pedido",
      description =
          """
          Crea un pedido para el usuario autenticado.
          Si `fulfillment = DELIVERY`, se guarda el snapshot de dirección en el pedido.
          """,
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema =
                          @Schema(
                              implementation =
                                  com.cocinadelicia.backend.order.dto.CreateOrderRequest.class),
                      examples =
                          @ExampleObject(
                              name = "DELIVERY",
                              value =
                                  """
                  {
                    "fulfillment": "DELIVERY",
                    "items": [
                      { "productId": 1, "productVariantId": 10, "quantity": 2 }
                    ],
                    "notes": "Sin cebolla",
                    "shipping": {
                      "name": "Eduardo",
                      "phone": "099123123",
                      "line1": "Calle 123",
                      "line2": "Apto 4",
                      "city": "Montevideo",
                      "region": "Montevideo",
                      "postalCode": "11000",
                      "reference": "Puerta verde"
                    }
                  }
                  """))),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Pedido creado",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Request inválido"),
        @ApiResponse(responseCode = "404", description = "Usuario no registrado en app_user")
      })
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateOrderRequest request) {
    Long appUserId = resolveAppUserId(jwt);
    OrderResponse response = orderService.createOrder(request, appUserId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Obtener detalle de un pedido propio",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "404", description = "No existe o no pertenece al usuario")
      })
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    Long appUserId = resolveAppUserId(jwt);
    return ResponseEntity.ok(orderService.getOrderById(id, appUserId));
  }

  @Operation(
      summary = "Listar mis pedidos (paginado, orden por fecha de creación DESC por defecto)")
  @GetMapping("/mine")
  public ResponseEntity<Page<OrderResponse>> getMyOrders(
      @AuthenticationPrincipal Jwt jwt,
      @ParameterObject
          @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Long appUserId = resolveAppUserId(jwt);
    return ResponseEntity.ok(orderService.getMyOrders(appUserId, pageable));
  }

  @Operation(summary = "Listar pedidos (ADMIN/CHEF)", description = "Listado global, paginado")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
  public ResponseEntity<Page<OrderResponse>> getAll(
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(orderService.getAllOrders(pageable));
  }

  @Operation(summary = "Cambiar estado de un pedido (ADMIN/CHEF)")
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
  public ResponseEntity<OrderResponse> updateStatus(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable Long id,
      @Valid @RequestBody UpdateOrderStatusRequest body) {

    // Para auditoría: preferimos email si está; sino sub
    String performer = jwt.getClaimAsString("email");
    if (performer == null || performer.isBlank()) {
      performer = jwt.getSubject();
    }

    OrderResponse response = orderService.updateStatus(id, performer, body);
    return ResponseEntity.ok(response);
  }

  private Long resolveAppUserId(Jwt jwt) {
    String sub = jwt.getClaim("sub");
    return userRepository
        .findByCognitoUserId(sub)
        .map(u -> u.getId())
        .orElseThrow(
            () -> new NotFoundException("Usuario no registrado en app_user para sub=" + sub));
  }
}
