package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "price_history",
    indexes = {
      @Index(name = "ix_price_hist_from", columnList = "product_variant_id, valid_from"),
      @Index(name = "ix_price_hist_to", columnList = "product_variant_id, valid_to")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE price_history SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class PriceHistory extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_variant_id", nullable = false)
  private ProductVariant productVariant;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  @Builder.Default
  private CurrencyCode currency = CurrencyCode.UYU;

  @Column(name = "valid_from", nullable = false)
  private Instant validFrom;

  @Column(name = "valid_to")
  private Instant validTo;
}
