package com.cocinadelicia.backend.catalog.admin.controller;

import com.cocinadelicia.backend.catalog.admin.service.AdminModifierGroupService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierOptionService;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminResponse;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/modifier-groups")
@RequiredArgsConstructor
@Tag(
    name = "admin-modifiers",
    description = "Administración de grupos y opciones de modificadores (solo ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModifierController {

  private final AdminModifierGroupService groupService;
  private final AdminModifierOptionService optionService;

  @Operation(summary = "Listar grupos de modificadores por variante")
  @GetMapping
  public ResponseEntity<List<ModifierGroupAdminResponse>> listByVariant(
      @RequestParam Long productVariantId) {
    return ResponseEntity.ok(groupService.getByVariant(productVariantId));
  }

  @Operation(summary = "Obtener grupo de modificadores por ID")
  @GetMapping("/{id}")
  public ResponseEntity<ModifierGroupAdminResponse> getGroup(@PathVariable Long id) {
    return ResponseEntity.ok(groupService.getById(id));
  }

  @Operation(summary = "Crear grupo de modificadores")
  @PostMapping
  public ResponseEntity<ModifierGroupAdminResponse> createGroup(
      @Valid @RequestBody ModifierGroupAdminRequest request) {
    return ResponseEntity.ok(groupService.create(request));
  }

  @Operation(summary = "Actualizar grupo de modificadores")
  @PutMapping("/{id}")
  public ResponseEntity<ModifierGroupAdminResponse> updateGroup(
      @PathVariable Long id, @Valid @RequestBody ModifierGroupAdminRequest request) {
    return ResponseEntity.ok(groupService.update(id, request));
  }

  @Operation(summary = "Eliminar grupo de modificadores")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
    groupService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Listar grupos de modificadores por producto (todas las variantes)")
  @GetMapping("/catalog/products/{productId}")
  public ResponseEntity<List<ModifierGroupAdminResponse>> listByProduct(
      @PathVariable Long productId) {
    return ResponseEntity.ok(groupService.getByProduct(productId));
  }

  // ------- Opciones -------

  @Operation(summary = "Listar opciones de un grupo")
  @GetMapping("/{groupId}/options")
  public ResponseEntity<List<ModifierOptionAdminResponse>> listOptions(@PathVariable Long groupId) {
    return ResponseEntity.ok(optionService.getByGroup(groupId));
  }

  @Operation(summary = "Obtener opción de modificador por ID")
  @GetMapping("/options/{id}")
  public ResponseEntity<ModifierOptionAdminResponse> getOption(@PathVariable Long id) {
    return ResponseEntity.ok(optionService.getById(id));
  }

  @Operation(summary = "Crear opción dentro de un grupo")
  @PostMapping("/{groupId}/options")
  public ResponseEntity<ModifierOptionAdminResponse> createOption(
      @PathVariable Long groupId, @Valid @RequestBody ModifierOptionAdminRequest request) {
    return ResponseEntity.ok(optionService.create(groupId, request));
  }

  @Operation(summary = "Actualizar opción de modificador")
  @PutMapping("/options/{id}")
  public ResponseEntity<ModifierOptionAdminResponse> updateOption(
      @PathVariable Long id, @Valid @RequestBody ModifierOptionAdminRequest request) {
    return ResponseEntity.ok(optionService.update(id, request));
  }

  @Operation(summary = "Eliminar opción de modificador")
  @DeleteMapping("/options/{id}")
  public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
    optionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
