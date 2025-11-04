package com.cocinadelicia.backend.order.adapter;

import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.product.repository.PriceHistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaPriceHistoryAdapter implements PriceQueryPort {

  private final PriceHistoryRepository priceHistoryRepository;

  @Override
  public Optional<BigDecimal> findActivePriceByVariantId(Long productVariantId) {
    return priceHistoryRepository.findActivePrice(productVariantId, Instant.now());
  }
}
