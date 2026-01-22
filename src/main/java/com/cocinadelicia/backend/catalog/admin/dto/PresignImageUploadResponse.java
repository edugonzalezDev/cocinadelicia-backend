package com.cocinadelicia.backend.catalog.admin.dto;

import java.util.Map;

public record PresignImageUploadResponse(
    String uploadUrl,
    String objectKey,
    String publicUrl,
    int expiresInSeconds,
    Map<String, String> requiredHeaders) {}
