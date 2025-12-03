// src/main/java/com/cocinadelicia/backend/product/service/impl/PriceServiceImpl.java
package com.cocinadelicia.backend.product.service.impl;

import com.cocinadelicia.backend.product.repository.PriceHistoryRepository;
import com.cocinadelicia.backend.product.service.PriceService;
import com.cocinadelicia.backend.product.service.dto.PriceInfo;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

  private final PriceHistoryRepository priceHistoryRepository;

  @Override
  public Optional<PriceInfo> getCurrentPriceForVariant(Long variantId) {
    Instant now = Instant.now();
    return priceHistoryRepository
        .findActivePriceEntry(variantId, now)
        .map(
            ph ->
                new PriceInfo(ph.getPrice(), ph.getCurrency(), ph.getValidFrom(), ph.getValidTo()));
  }
}
