package com.cocinadelicia.backend.product.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Payload para actualizar precio vigente de una variante")
public record ProductVariantPriceUpdateRequest(
    @NotNull @DecimalMin("0.00") @Schema(description = "Precio vigente", example = "250.00")
        BigDecimal price,
    @Schema(description = "Moneda (por defecto UYU)", example = "UYU") CurrencyCode currency) {}
