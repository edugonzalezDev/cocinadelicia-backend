package com.cocinadelicia.backend.catalog.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PresignImageUploadRequest(
    @NotBlank String contentType, @Min(1) @Max(819200) long sizeBytes) {}
