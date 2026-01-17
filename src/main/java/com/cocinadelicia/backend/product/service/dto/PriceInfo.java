// src/main/java/com/cocinadelicia/backend/product/service/dto/PriceInfo.java
package com.cocinadelicia.backend.product.service.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Información de precio vigente para una variante. Usado internamente por el dominio (no es DTO de
 * API).
 */
public record PriceInfo(
    BigDecimal amount, CurrencyCode currency, Instant validFrom, Instant validTo) {}
