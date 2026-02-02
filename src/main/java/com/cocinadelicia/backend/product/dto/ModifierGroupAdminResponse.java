package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Grupo de modificadores (admin)")
public record ModifierGroupAdminResponse(
    Long id,
    Long productVariantId,
    String name,
    int minSelect,
    int maxSelect,
    String selectionMode,
    Integer requiredTotalQty,
    Long defaultOptionId,
    int sortOrder,
    boolean active,
    List<ModifierOptionAdminResponse> options) {}
