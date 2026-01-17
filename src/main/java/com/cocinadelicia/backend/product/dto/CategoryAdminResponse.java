package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoría para administración")
public record CategoryAdminResponse(Long id, String name, String slug, String description) {}
