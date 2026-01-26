// src/main/java/com/cocinadelicia/backend/catalog/dto/ProductSummaryResponse.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resumen de producto para el catálogo público")
public record ProductSummaryResponse(
    @Schema(description = "Id del producto", example = "42") Long id,
    @Schema(description = "Nombre del producto", example = "Empanadas de carne caseras")
        String name,
    @Schema(
            description = "Slug único del producto, usado para URLs amigables",
            example = "empanadas-carne-caseras")
        String slug,
    @Schema(
            description = "Descripción corta pensada para la card del catálogo",
            example = "Masa casera, relleno de carne cortada a cuchillo.")
        String shortDescription,

    // Categoría
    @Schema(description = "Id de la categoría del producto", example = "1") Long categoryId,
    @Schema(description = "Nombre de la categoría", example = "Empanadas") String categoryName,
    @Schema(
            description = "Slug de la categoría (para filtros por categoría)",
            example = "empanadas")
        String categorySlug,

    // Imagen principal
    @Schema(
            description =
                "URL principal de la imagen del producto (puede ser null si no hay imagen)",
            example = "https://cdn.lacocinadelicia.com/products/empanadas-carne-main.jpg")
        String mainImageUrl,

    // imagenes
    @Schema(
            description = "Lista de URLs de imágenes del producto",
            example =
                "[\"https://cdn.lacocinadelicia.com/products/empanadas-carne-1.jpg\", \"https://cdn.lacocinadelicia.com/products/empanadas-carne-2.jpg\"]")
        List<String> imageUrls,

    // Precio "desde"
    @Schema(
            description = "Precio mínimo entre las variantes visibles del producto",
            example = "{ \"amount\": 120.00, \"currency\": \"UYU\" }")
        MoneyResponse fromPrice,

    // Disponibilidad a alto nivel
    @Schema(
            description =
                "Indicador de disponibilidad a alto nivel (true: al menos una variante disponible)",
            example = "true")
        boolean available,
    @Schema(
            description =
                "Indica si el producto es principalmente a pedido (no depende de stock físico)",
            example = "true")
        boolean madeToOrder,
    @Schema(
            description = "Indica si alguna variante maneja stock real (true) o no (false)",
            example = "false")
        boolean managesStock,

    // Flags de marketing
    @Schema(description = "Indica si el producto está destacado en el catálogo", example = "true")
        boolean featured,
    @Schema(description = "Indica si el producto forma parte del menú del día", example = "false")
        boolean dailyMenu,
    @Schema(description = "Indica si el producto es \"nuevo\" en el catálogo", example = "true")
        boolean isNew,

    // Variantes
    @Schema(description = "Lista de variantes disponibles para el producto en el catálogo")
        List<CatalogVariantResponse> variants,

    // Tags
    @Schema(
            description = "Lista de slugs de tags asociados al producto",
            example = "[\"sin-tacc\", \"picante\"]")
        List<String> tags) {}
