// src/main/java/com/cocinadelicia/backend/product/service/PriceService.java
package com.cocinadelicia.backend.product.service;

import com.cocinadelicia.backend.product.service.dto.PriceInfo;
import java.util.Optional;

public interface PriceService {

  /**
   * Devuelve el precio vigente para una variante, si existe.
   *
   * <p>Regla de negocio: - validFrom <= now - validTo is null OR validTo > now
   */
  Optional<PriceInfo> getCurrentPriceForVariant(Long variantId);
}
