package com.cocinadelicia.backend.catalog.admin.dto;

public record ProductImageAdminResponse(
    Long id, String objectKey, String url, boolean isMain, int sortOrder) {}
