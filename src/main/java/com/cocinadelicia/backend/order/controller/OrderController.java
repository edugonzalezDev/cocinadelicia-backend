package com.cocinadelicia.backend.order.controller;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderFilter;
import com.cocinadelicia.backend.order.dto.OrderPageResponse;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import com.cocinadelicia.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operaciones de pedidos del usuario autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;
  private final CurrentUserService currentUserService;

  // ---------- Cliente autenticado ----------
  @Operation(summary = "Crear un pedido")
  @ApiResponse(
      responseCode = "201",
      description = "Pedido creado",
      content = @Content(schema = @Schema(implementation = OrderResponse.class)))
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
    @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateOrderRequest request) {
    Long appUserId = currentUserService.getOrCreateCurrentUserId(); // 👈 ahora crea si falta
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.createOrder(request, appUserId));
  }

  @Operation(summary = "Obtener detalle de un pedido propio")
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
    Long appUserId = userService.resolveUserIdFromJwt(jwt);
    return ResponseEntity.ok(orderService.getOrderById(id, appUserId));
  }

  @Operation(summary = "Listar mis pedidos (paginado, por defecto createdAt DESC)")
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
      summary = "Listar pedidos con filtros opcionales (ADMIN o CHEF)",
      description = "Filtro por status CSV y fechas from/to (YYYY-MM-DD). Size máx 50.")
  @GetMapping({"/ops", "/admin", "/chef"})
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
  public ResponseEntity<OrderPageResponse<OrderResponse>> listForBackoffice(
      @RequestParam(name = "status", required = false) String statusCsv,
      @RequestParam(name = "from", required = false) String fromStr,
      @RequestParam(name = "to", required = false) String toStr,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    Pageable safe = safePageable(pageable, 50);
    List<OrderStatus> statuses = parseStatuses(statusCsv);
    LocalDate from = parseDate(fromStr);
    LocalDate to = parseDate(toStr);

    var filter = new OrderFilter(statuses, from, to, null);
    var page = orderService.findOrders(filter, safe);
    return ResponseEntity.ok(OrderPageResponse.from(page));
  }

  @Operation(summary = "Cambiar estado de un pedido (ADMIN o CHEF)")
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
