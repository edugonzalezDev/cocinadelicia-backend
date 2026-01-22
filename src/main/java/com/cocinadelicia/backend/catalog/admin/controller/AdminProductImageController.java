package com.cocinadelicia.backend.catalog.admin.controller;

import com.cocinadelicia.backend.catalog.admin.dto.PresignImageUploadRequest;
import com.cocinadelicia.backend.catalog.admin.dto.PresignImageUploadResponse;
import com.cocinadelicia.backend.common.s3.ImagePresignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/catalog/products")
@RequiredArgsConstructor
public class AdminProductImageController {

  private final ImagePresignService imagePresignService;

  @PostMapping("/{productId}/images/presign")
  public ResponseEntity<PresignImageUploadResponse> presignUpload(
      @PathVariable long productId, @Valid @RequestBody PresignImageUploadRequest body) {

    var res = imagePresignService.presignProductImageUpload(productId, body.contentType());

    return ResponseEntity.ok(
        new PresignImageUploadResponse(
            res.uploadUrl(), res.objectKey(), res.publicUrl(), 600, res.requiredHeaders()));
  }
}
