// src/main/java/com/cocinadelicia/backend/catalog/dto/CatalogVariantResponse.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Variante de producto para el catálogo público")
public record CatalogVariantResponse(
    @Schema(description = "Id de la variante", example = "101") Long id,
    @Schema(description = "Nombre de la variante", example = "Docena") String name,
    @Schema(description = "Precio vigente de la variante") MoneyResponse price,
    @Schema(description = "Indica si la variante maneja stock real", example = "true")
        boolean managesStock,
    @Schema(description = "Cantidad disponible si maneja stock", example = "12") int stockQuantity,
    @Schema(
            description = "Etiqueta de disponibilidad para mostrar en la UI",
            example = "Disponible")
        String availabilityLabel,
    @Schema(description = "Grupos de modificadores disponibles para esta variante")
        List<ModifierGroupCatalogResponse> modifiers) {}
