package com.cocinadelicia.backend.catalog.admin.controller;

import com.cocinadelicia.backend.catalog.admin.service.AdminCategoryService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductVariantService;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
@Tag(name = "admin-catalog", description = "Administración de catálogo (solo ADMIN)")
public class AdminCatalogController {

  private final AdminCategoryService categoryService;
  private final AdminProductService productService;
  private final AdminProductVariantService variantService;

  // ---------- Categorías ----------

  @Operation(summary = "Listar categorías (admin)")
  @GetMapping("/categories")
  public ResponseEntity<List<CategoryAdminResponse>> getCategories() {
    return ResponseEntity.ok(categoryService.getAll());
  }

  @Operation(summary = "Obtener categoría por ID (admin)")
  @GetMapping("/categories/{id}")
  public ResponseEntity<CategoryAdminResponse> getCategory(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.getById(id));
  }

  @Operation(summary = "Crear nueva categoría (admin)")
  @PostMapping("/categories")
  public ResponseEntity<CategoryAdminResponse> createCategory(
      @Valid @RequestBody CategoryAdminRequest request) {
    return ResponseEntity.ok(categoryService.create(request));
  }

  @Operation(summary = "Actualizar categoría (admin)")
  @PutMapping("/categories/{id}")
  public ResponseEntity<CategoryAdminResponse> updateCategory(
      @PathVariable Long id, @Valid @RequestBody CategoryAdminRequest request) {
    return ResponseEntity.ok(categoryService.update(id, request));
  }

  @Operation(summary = "Eliminar categoría (admin)")
  @DeleteMapping("/categories/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // ---------- Productos ----------

  @Operation(summary = "Listar productos (admin)")
  @GetMapping("/products")
  public ResponseEntity<PageResponse<ProductAdminResponse>> getProducts(
      @RequestParam(name = "categoryId", required = false) Long categoryId,
      @RequestParam(name = "active", required = false) Boolean isActive,
      @ParameterObject @PageableDefault(size = 20) Pageable pageable) {

    PageResponse<ProductAdminResponse> page =
        productService.getProducts(categoryId, isActive, pageable);
    return ResponseEntity.ok(page);
  }

  @Operation(summary = "Obtener producto por ID (admin)")
  @GetMapping("/products/{id}")
  public ResponseEntity<ProductAdminResponse> getProduct(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getById(id));
  }

  @Operation(summary = "Crear nuevo producto (admin)")
  @PostMapping("/products")
  public ResponseEntity<ProductAdminResponse> createProduct(
      @Valid @RequestBody ProductAdminRequest request) {
    return ResponseEntity.ok(productService.create(request));
  }

  @Operation(summary = "Actualizar producto (admin)")
  @PutMapping("/products/{id}")
  public ResponseEntity<ProductAdminResponse> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductAdminRequest request) {
    return ResponseEntity.ok(productService.update(id, request));
  }

  @Operation(summary = "Eliminar producto (admin)")
  @DeleteMapping("/products/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // ---------- Variantes ----------

  @Operation(summary = "Listar variantes de un producto (admin)")
  @GetMapping("/products/{productId}/variants")
  public ResponseEntity<List<ProductVariantAdminResponse>> getVariantsByProduct(
      @PathVariable Long productId) {
    return ResponseEntity.ok(variantService.getByProductId(productId));
  }

  @Operation(summary = "Obtener variante por ID (admin)")
  @GetMapping("/variants/{id}")
  public ResponseEntity<ProductVariantAdminResponse> getVariant(@PathVariable Long id) {
    return ResponseEntity.ok(variantService.getById(id));
  }

  @Operation(summary = "Crear nueva variante para un producto (admin)")
  @PostMapping("/products/{productId}/variants")
  public ResponseEntity<ProductVariantAdminResponse> createVariant(
      @PathVariable Long productId, @Valid @RequestBody ProductVariantAdminRequest request) {
    return ResponseEntity.ok(variantService.create(productId, request));
  }

  @Operation(summary = "Actualizar variante (admin, parcial)")
  @PatchMapping("/variants/{id}")
  public ResponseEntity<ProductVariantAdminResponse> updateVariant(
      @PathVariable Long id, @RequestBody ProductVariantAdminRequest request) {
    // Al ser parcial, no usamos @Valid a nivel global del DTO,
    // las validaciones las manejamos dentro del servicio (ej: stock >= 0)
    return ResponseEntity.ok(variantService.update(id, request));
  }

  @Operation(summary = "Eliminar variante (admin)")
  @DeleteMapping("/variants/{id}")
  public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
    variantService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
