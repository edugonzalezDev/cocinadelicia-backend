package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Detalle completo de un producto para el catálogo público")
public record ProductDetailResponse(
    @Schema(description = "Id del producto", example = "10") Long id,
    @Schema(description = "Slug único del producto", example = "milanesa-clasica") String slug,
    @Schema(description = "Nombre del producto", example = "Milanesa clásica") String name,
    @Schema(description = "Descripción larga del producto") String description,
    @Schema(description = "Descripción corta", example = "Crujiente y deliciosa")
        String shortDescription,
    @Schema(description = "Id de la categoría", example = "1") Long categoryId,
    @Schema(description = "Nombre de la categoría", example = "Platos") String categoryName,
    @Schema(description = "Slug de la categoría", example = "platos") String categorySlug,
    @Schema(description = "URL de la imagen principal") String mainImageUrl,
    @Schema(description = "Galería de imágenes") List<CatalogImageResponse> images,
    @Schema(description = "Precio mínimo entre las variantes visibles") MoneyResponse fromPrice,
    @Schema(description = "Variantes disponibles del producto")
        List<CatalogVariantResponse> variants,
    @Schema(description = "Slugs de tags asociados") List<String> tags,
    @Schema(description = "Indicador de producto destacado", example = "true") boolean featured,
    @Schema(description = "Indicador de menú del día", example = "false") boolean dailyMenu,
    @Schema(description = "Indicador de producto nuevo", example = "false") boolean isNew,
    @Schema(description = "Indicador de disponibilidad general", example = "true")
        boolean available,
    @Schema(description = "Indica si alguna variante maneja stock", example = "true")
        boolean managesStock,
    @Schema(description = "Indica si el producto es principalmente a pedido", example = "true")
        boolean madeToOrder,
    @Schema(description = "Grupos de modificadores disponibles para las variantes")
        List<ModifierGroupCatalogResponse> modifierGroups) {}
