package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Imagen de producto para el catálogo público")
public record CatalogImageResponse(
    @Schema(description = "Id de la imagen", example = "1") Long id,
    @Schema(description = "URL pública de la imagen", example = "https://cdn.lacocinadelicia.com/products/empanada-1.jpg")
        String url,
    @Schema(description = "Indica si es la imagen principal", example = "true") boolean isMain,
    @Schema(description = "Orden de visualización", example = "0") int sortOrder,
    @Schema(description = "Texto alternativo sugerido", example = "Milanesa clásica") String alt) {}
