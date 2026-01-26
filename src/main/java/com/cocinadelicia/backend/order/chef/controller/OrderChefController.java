package com.cocinadelicia.backend.order.chef.controller;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.chef.dto.OrderChefResponse;
import com.cocinadelicia.backend.order.chef.service.OrderChefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chef/orders")
@RequiredArgsConstructor
@Tag(name = "Chef Orders", description = "Gestión de pedidos para chefs")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('CHEF')")
public class OrderChefController {

  private final OrderChefService orderChefService;

  @GetMapping
  @Operation(
      summary = "Obtener órdenes asignadas al chef",
      description = "Lista todas las órdenes asignadas al chef autenticado")
  public ResponseEntity<Page<OrderChefResponse>> getAssignedOrders(
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(orderChefService.getAssignedOrders(pageable));
  }

  @GetMapping("/active")
  @Operation(
      summary = "Obtener órdenes activas",
      description = "Lista solo las órdenes en estados CONFIRMED, PREPARING, READY asignadas al chef")
  public ResponseEntity<Page<OrderChefResponse>> getActiveOrders(
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC)
          Pageable pageable) {
    return ResponseEntity.ok(orderChefService.getActiveOrders(pageable));
  }

  @PatchMapping("/{id}/status")
  @Operation(
      summary = "Actualizar estado de una orden",
      description =
          "Chef solo puede cambiar de CONFIRMED → PREPARING o PREPARING → READY")
  public ResponseEntity<OrderChefResponse> updateStatus(
      @PathVariable Long id,
      @Parameter(description = "Nuevo estado", required = true) @RequestParam OrderStatus status) {
    return ResponseEntity.ok(orderChefService.updateStatus(id, status));
  }
}
