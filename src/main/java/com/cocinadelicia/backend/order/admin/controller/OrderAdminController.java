package com.cocinadelicia.backend.order.admin.controller;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.admin.dto.CreateOrderAdminRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderFilterRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderStatsResponse;
import com.cocinadelicia.backend.order.admin.service.OrderAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Gestión de pedidos para administradores")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

  private final OrderAdminService orderAdminService;

  @PostMapping
  @Operation(
      summary = "Crear orden en nombre de un cliente (Admin)",
      description =
          """
      Permite al administrador crear una orden en nombre de un cliente.
      Requiere el email del cliente (debe existir en el sistema).
      Útil para órdenes telefónicas o por otros canales.
      """)
  public ResponseEntity<OrderAdminResponse> createOrderForUser(
      @Valid @RequestBody CreateOrderAdminRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderAdminService.createOrderForUser(request));
  }

  @GetMapping
  @Operation(
      summary = "Listar todas las órdenes con filtros avanzados",
      description =
          "Obtiene lista paginada de órdenes con filtros por estado, fecha, usuario, chef, monto, etc.")
  public ResponseEntity<Page<OrderAdminResponse>> getAllOrders(
      @ParameterObject @Valid OrderFilterRequest filters,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(orderAdminService.getAllOrders(filters, pageable));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Obtener detalle completo de una orden",
      description =
          "Incluye historial de cambios de estado, datos del usuario, chef asignado, etc.")
  public ResponseEntity<OrderAdminResponse> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderAdminService.getOrderById(id));
  }

  @PatchMapping("/{id}/status")
  @Operation(
      summary = "Actualizar estado de una orden",
      description =
          "Permite cambiar el estado de una orden. Admin puede hacer cualquier transición válida.")
  public ResponseEntity<OrderAdminResponse> updateStatus(
      @PathVariable Long id,
      @Parameter(description = "Nuevo estado", required = true) @RequestParam OrderStatus status,
      @Parameter(description = "Razón del cambio (opcional)") @RequestParam(required = false)
          String reason) {
    return ResponseEntity.ok(orderAdminService.updateStatus(id, status, reason));
  }

  @PatchMapping("/{id}/assign-chef")
  @Operation(
      summary = "Asignar chef a una orden",
      description = "Asigna un chef específico para preparar la orden")
  public ResponseEntity<OrderAdminResponse> assignChef(
      @PathVariable Long id,
      @Parameter(description = "Email del chef", required = true) @RequestParam String chefEmail) {
    return ResponseEntity.ok(orderAdminService.assignChef(id, chefEmail));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Eliminar orden (soft delete)",
      description = "Marca la orden como eliminada y cambia su estado a CANCELLED")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderAdminService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/stats")
  @Operation(
      summary = "Obtener estadísticas de órdenes",
      description =
          "KPIs para dashboard: órdenes por estado, ingresos del día, ticket promedio, etc.")
  public ResponseEntity<OrderStatsResponse> getStats() {
    return ResponseEntity.ok(orderAdminService.getStats());
  }
}
