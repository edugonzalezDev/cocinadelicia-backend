package com.cocinadelicia.backend.catalog.admin.controller;

import com.cocinadelicia.backend.catalog.admin.dto.PresignImageUploadRequest;
import com.cocinadelicia.backend.catalog.admin.dto.PresignImageUploadResponse;
import com.cocinadelicia.backend.common.s3.ImagePresignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/catalog/products")
@RequiredArgsConstructor
@Tag(
    name = "admin-product-images",
    description = "Presign para upload de imágenes de productos (solo ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductImageController {

  private final ImagePresignService imagePresignService;

  @PostMapping("/{productId}/images/presign")
  @Operation(summary = "Generar URL presignada para upload de imagen")
  public ResponseEntity<PresignImageUploadResponse> presignUpload(
      @PathVariable long productId, @Valid @RequestBody PresignImageUploadRequest body) {

    var res = imagePresignService.presignProductImageUpload(productId, body.contentType());

    return ResponseEntity.ok(
        new PresignImageUploadResponse(
            res.uploadUrl(), res.objectKey(), res.publicUrl(), 600, res.requiredHeaders()));
  }
}
