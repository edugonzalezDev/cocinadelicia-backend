// src/main/java/com/cocinadelicia/backend/product/repository/PriceHistoryRepository.java
package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.PriceHistory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

  @Query(
"""
  select ph.price
  from PriceHistory ph
  where ph.productVariant.id = :variantId
    and ph.validFrom <= :now
    and (ph.validTo is null or ph.validTo > :now)
  order by ph.validFrom desc
""")
  Optional<BigDecimal> findActivePrice(
      @Param("variantId") Long variantId, @Param("now") Instant now);

  @Query(
"""
  select ph
  from PriceHistory ph
  where ph.productVariant.id = :variantId
    and ph.validFrom <= :now
    and (ph.validTo is null or ph.validTo > :now)
  order by ph.validFrom desc
""")
  Optional<PriceHistory> findActivePriceEntry(
      @Param("variantId") Long variantId, @Param("now") Instant now);
}
