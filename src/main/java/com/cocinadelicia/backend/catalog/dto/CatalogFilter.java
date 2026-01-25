// src/main/java/com/cocinadelicia/backend/catalog/dto/CatalogFilter.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

@Schema(description = "Filtros para búsqueda de productos del catálogo público")
public record CatalogFilter(
    @Schema(description = "Texto de búsqueda (busca en nombre, descripción y tags)", example = "milanesa")
        String searchQuery,
    @Schema(description = "Slug de la categoría a filtrar (opcional)", example = "empanadas")
        String categorySlug,
    @Schema(description = "Número de página (0-based)", example = "0") int page,
    @Schema(description = "Tamaño de página", example = "12") int size,
    Sort sort,
    @Schema(
            description = "Filtrar solo productos con al menos una variante destacada",
            example = "true")
        Boolean featured,
    @Schema(
            description =
                "Filtrar solo productos cuyo menú del día esté activo (al menos una variante)",
            example = "true")
        Boolean dailyMenu,
    @Schema(
            description =
                "Filtrar solo productos \"nuevos\" (al menos una variante marcada como nueva)",
            example = "true")
        Boolean isNew,
    @Schema(
            description = "Si true, solo productos activos con variantes disponibles (con stock si managesStock=true)",
            example = "true")
        Boolean availableOnly) {}
