// src/main/java/com/cocinadelicia/backend/catalog/dto/CatalogFilter.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

@Schema(description = "Filtros para búsqueda de productos del catálogo público")
public record CatalogFilter(
  @Schema(
    description = "Slug de la categoría a filtrar (opcional)",
    example = "empanadas")
  String categorySlug,
  @Schema(description = "Número de página (0-based)", example = "0")
  int page,
  @Schema(description = "Tamaño de página", example = "12")
  int size,
  Sort sort
) {}
