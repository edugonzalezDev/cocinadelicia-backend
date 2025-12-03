// src/main/java/com/cocinadelicia/backend/catalog/dto/MoneyResponse.java
package com.cocinadelicia.backend.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Representación de un monto de dinero")
public record MoneyResponse(
  @Schema(description = "Monto numérico", example = "120.00")
  BigDecimal amount,
  @Schema(description = "Código de moneda ISO 4217", example = "UYU")
  String currency
) {}
