package com.cocinadelicia.backend.catalog.admin.controller;

import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminPatchRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminResponse;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductImageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/catalog")
public class AdminProductImageManagementController {

  private final AdminProductImageService service;

  @GetMapping("/products/{productId}/images")
  public ResponseEntity<List<ProductImageAdminResponse>> list(@PathVariable long productId) {
    return ResponseEntity.ok(service.listByProduct(productId));
  }

  @PostMapping("/products/{productId}/images")
  public ResponseEntity<ProductImageAdminResponse> add(
      @PathVariable long productId, @Valid @RequestBody ProductImageAdminRequest body) {
    return ResponseEntity.ok(service.addToProduct(productId, body));
  }

  @PatchMapping("/images/{imageId}")
  public ResponseEntity<ProductImageAdminResponse> patch(
      @PathVariable long imageId, @RequestBody ProductImageAdminPatchRequest body) {
    return ResponseEntity.ok(service.patch(imageId, body));
  }

  @DeleteMapping("/images/{imageId}")
  public ResponseEntity<Void> delete(@PathVariable long imageId) {
    service.delete(imageId);
    return ResponseEntity.noContent().build();
  }
}
