// src/main/java/com/cocinadelicia/backend/product/repository/PriceHistoryRepository.java
package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.PriceHistory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @Query(
      """
      select ph
      from PriceHistory ph
      where ph.productVariant.id = :variantId
        and ph.validTo is null
      order by ph.validFrom desc
      """)
  Optional<PriceHistory> findActivePriceByVariantId(@Param("variantId") Long variantId);

  @Query(
      value =
          """
          select *
          from price_history
          where product_variant_id = :variantId
            and valid_to is null
            and deleted_at is null
          order by valid_from desc
          limit 1
          for update
          """,
      nativeQuery = true)
  Optional<PriceHistory> findActivePriceEntryForUpdate(@Param("variantId") Long variantId);

  /**
   * Query batch optimizada para obtener precios actuales de múltiples variantes. Reduce N+1 queries
   * a una sola consulta.
   */
  @Query(
"""
  select ph
  from PriceHistory ph
  where ph.productVariant.id in :variantIds
    and ph.validFrom <= :now
    and (ph.validTo is null or ph.validTo > :now)
  order by ph.productVariant.id, ph.validFrom desc
""")
  List<PriceHistory> findCurrentPricesForVariants(
      @Param("variantIds") Set<Long> variantIds, @Param("now") Instant now);

  /** Sobrecarga con timestamp actual por defecto. */
  default List<PriceHistory> findCurrentPricesForVariants(Set<Long> variantIds) {
    return findCurrentPricesForVariants(variantIds, Instant.now());
  }
}
