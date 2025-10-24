package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "product_variant",
    uniqueConstraints = @UniqueConstraint(name = "uk_variant_sku", columnNames = "sku"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_variant SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductVariant extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(length = 191)
  private String sku;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PriceHistory> priceHistory = new ArrayList<>();
}
