// src/main/java/com/cocinadelicia/backend/catalog/dto/CategorySummaryResponse.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de categoría para el catálogo público")
public record CategorySummaryResponse(
    @Schema(description = "Id de la categoría", example = "1") Long id,
    @Schema(description = "Nombre visible de la categoría", example = "Platos del día") String name,
    @Schema(
            description = "Slug único de la categoría, usado para filtros y URLs amigables",
            example = "platos-del-dia")
        String slug,
    @Schema(
            description = "Descripción breve de la categoría (opcional)",
            example = "Opciones caseras que cambian día a día.")
        String description) {}
