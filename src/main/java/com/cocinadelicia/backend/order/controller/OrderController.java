package com.cocinadelicia.backend.order.controller;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.web.ApiError;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderFilter;
import com.cocinadelicia.backend.order.dto.OrderPageResponse;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import com.cocinadelicia.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "orders", description = "Operaciones de pedidos del usuario autenticado y backoffice")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;
  private final CurrentUserService currentUserService;

  // ---------- Cliente autenticado ----------
  @Operation(
      summary = "Crear un nuevo pedido",
      description =
          """
        Crea un nuevo pedido asociado al usuario autenticado (Cognito).
        Requiere token JWT válido. El subtotal y total se calculan en base a los precios vigentes de cada variante.
        """,
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Pedido creado correctamente",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Request inválido (validación de negocio o @Valid)",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado")
      })
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateOrderRequest request) {
    Long appUserId = currentUserService.getOrCreateCurrentUserId();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.createOrder(request, appUserId));
  }

  @Operation(
      summary = "Obtener detalle de un pedido propio",
      description =
          """
        Devuelve el detalle de un pedido perteneciente al usuario autenticado.
        Si el pedido no existe o no pertenece al usuario, devuelve 404.
        """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado o no pertenece al usuario",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    Long appUserId = userService.resolveUserIdFromJwt(jwt);
    return ResponseEntity.ok(orderService.getOrderById(id, appUserId));
  }

  @Operation(
      summary = "Listar mis pedidos",
      description =
          """
        Lista paginada de pedidos del usuario autenticado, ordenados por defecto por createdAt DESC.
        """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Página de pedidos del usuario",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  @GetMapping("/mine")
  public ResponseEntity<Page<OrderResponse>> getMyOrders(
      @AuthenticationPrincipal Jwt jwt,
      @ParameterObject
          @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Long appUserId = currentUserService.getOrCreateCurrentUserId();
    return ResponseEntity.ok(orderService.getMyOrders(appUserId, pageable));
  }

  // ---------- Staff (ADMIN o CHEF) ----------
  @Operation(
      summary = "Listar pedidos para backoffice / vista Chef",
      description =
          """
      Lista paginada de pedidos para backoffice (Admin/Chef).

      Casos de uso:
      - Vista administrativa general.
      - Vista de Chef con filtros por estado y fecha.

      Filtros opcionales:
      - status: lista CSV de estados (CREATED, PREPARING, READY, etc.).
      - from / to: fechas YYYY-MM-DD (ambas inclusivas, zona America/Montevideo).
      """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Página de pedidos filtrados",
            content = @Content(schema = @Schema(implementation = OrderPageResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Filtro inválido (status o fecha con formato incorrecto)",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN o CHEF)",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  @GetMapping({"/ops", "/admin", "/chef"})
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
  public ResponseEntity<OrderPageResponse<OrderResponse>> listForBackoffice(
      @AuthenticationPrincipal Jwt jwt,
      @Parameter(
              description =
                  "Estados CSV. Ej: CREATED,PREPARING,READY. Usa valores del enum OrderStatus.",
              example = "PREPARING,READY")
          @RequestParam(name = "status", required = false)
          String statusCsv,
      @Parameter(
              description = "Fecha desde (inclusive), formato YYYY-MM-DD.",
              example = "2025-11-01")
          @RequestParam(name = "from", required = false)
          String fromStr,
      @Parameter(
              description = "Fecha hasta (inclusive), formato YYYY-MM-DD.",
              example = "2025-11-30")
          @RequestParam(name = "to", required = false)
          String toStr,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    Pageable safe = safePageable(pageable, 50);
    List<OrderStatus> statuses = parseStatuses(statusCsv);
    LocalDate from = parseDate(fromStr);
    LocalDate to = parseDate(toStr);

    String performer = jwt.getClaimAsString("email");
    if (performer == null || performer.isBlank()) {
      performer = jwt.getSubject();
    }

    log.info(
        "ChefOrdersList performer={} statusFilter={} from={} to={} page={} size={}",
        performer,
        statuses,
        from,
        to,
        safe.getPageNumber(),
        safe.getPageSize());

    var filter = new OrderFilter(statuses, from, to, null);
    var page = orderService.findOrders(filter, safe);
    return ResponseEntity.ok(OrderPageResponse.from(page));
  }

  @Operation(
    summary = "Cambiar estado de un pedido (ADMIN o CHEF)",
    description =
      """
      Cambia el estado de un pedido existente. Requiere rol ADMIN o CHEF.

      Estados válidos (enum OrderStatus):
      - CREATED
      - CONFIRMED
      - PREPARING
      - READY
      - OUT_FOR_DELIVERY
      - DELIVERED
      - CANCELED

      Las transiciones válidas se validan en el backend.
      Ejemplos típicos:
      - CREATED -> PREPARING (válida)
      - PREPARING -> READY (válida)
      - READY -> DELIVERED (válida)
      - CREATED -> DELIVERED (inválida → 400 INVALID_STATUS_TRANSITION)
      """,
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Estado actualizado correctamente",
        content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(
        responseCode = "400",
        description = "Transición inválida o request inválido",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
      @ApiResponse(
        responseCode = "404",
        description = "Pedido no encontrado",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
      @ApiResponse(
        responseCode = "403",
        description = "Sin permisos (requiere rol ADMIN o CHEF)",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
      @ApiResponse(
        responseCode = "401",
        description = "No autenticado",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
  public ResponseEntity<OrderResponse> updateStatus(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long id,
    @Valid @RequestBody UpdateOrderStatusRequest body) {
    String performer = jwt.getClaimAsString("email");
    if (performer == null || performer.isBlank()) {
      performer = jwt.getSubject();
    }
    return ResponseEntity.ok(orderService.updateStatus(id, performer, body));
  }


  // ---------- Helpers ----------
  private Long resolveAppUserId(Jwt jwt) {
    return userService.resolveUserIdFromJwt(jwt);
  }

  private Pageable safePageable(Pageable pageable, int maxSize) {
    int size = Math.min(pageable.getPageSize(), maxSize);
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private List<OrderStatus> parseStatuses(String statusCsv) {
    if (statusCsv == null || statusCsv.isBlank()) return null;
    try {
      return Arrays.stream(statusCsv.split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .map(String::toUpperCase)
          .map(OrderStatus::valueOf)
          .distinct()
          .toList();
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException(
          "INVALID_STATUS", "Algún estado no es válido. Use valores del enum OrderStatus.");
    }
  }

  private LocalDate parseDate(String value) {
    if (value == null || value.isBlank()) return null;
    try {
      return LocalDate.parse(value);
    } catch (DateTimeParseException ex) {
      throw new BadRequestException("INVALID_DATE", "Las fechas deben tener formato YYYY-MM-DD.");
    }
  }
}
