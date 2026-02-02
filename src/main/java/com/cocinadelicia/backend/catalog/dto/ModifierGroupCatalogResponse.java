package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Grupo de modificadores para un producto/variante")
public record ModifierGroupCatalogResponse(
    @Schema(description = "Id del grupo", example = "10") Long id,
    @Schema(description = "Id de la variante a la que aplica", example = "20")
        Long productVariantId,
    @Schema(description = "Nombre del grupo", example = "Guarniciones") String name,
    @Schema(description = "Cantidad mínima de opciones", example = "0") int minSelect,
    @Schema(description = "Cantidad máxima de opciones", example = "2") int maxSelect,
    @Schema(description = "Modo de selección", example = "MULTI") String selectionMode,
    @Schema(description = "Cantidad total requerida cuando aplica", example = "6")
        Integer requiredTotalQty,
    @Schema(description = "Opción por defecto", example = "101") Long defaultOptionId,
    @Schema(description = "Orden de despliegue", example = "1") int sortOrder,
    @Schema(description = "Indica si está activo", example = "true") boolean active,
    @Schema(description = "Opciones disponibles") List<ModifierOptionCatalogResponse> options) {}
