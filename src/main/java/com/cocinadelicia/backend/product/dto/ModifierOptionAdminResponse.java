package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Opción de modificador (admin)")
public record ModifierOptionAdminResponse(
    Long id,
    Long groupId,
    String name,
    int sortOrder,
    boolean active,
    BigDecimal priceDelta,
    boolean exclusive,
    Long linkedProductVariantId) {}
