package com.cocinadelicia.backend.order.port;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceQueryPort {
  /**
   * Devuelve el precio vigente (decimal 10,2) para una variante. Vigente = valid_from <= now AND
   * (valid_to IS NULL OR valid_to > now).
   */
  Optional<BigDecimal> findActivePriceByVariantId(Long productVariantId);
}
