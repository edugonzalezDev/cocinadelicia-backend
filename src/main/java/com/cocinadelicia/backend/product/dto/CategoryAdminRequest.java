package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para crear/actualizar categoría (admin)")
public record CategoryAdminRequest(
    @Schema(description = "Nombre visible de la categoría", example = "Empanadas") String name,
    @Schema(description = "Slug único (URL friendly)", example = "empanadas") String slug,
    @Schema(
            description = "Descripción corta de la categoría",
            example = "Variedad de empanadas caseras")
        String description) {}
