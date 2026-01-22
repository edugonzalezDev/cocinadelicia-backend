package com.cocinadelicia.backend.catalog.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductImageAdminRequest(
    @NotBlank String objectKey, Boolean isMain, Integer sortOrder) {}
