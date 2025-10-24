package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "product",
    uniqueConstraints = @UniqueConstraint(name = "uk_product_slug", columnNames = "slug"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Product extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(length = 191, nullable = false)
  private String slug;

  @Lob private String description;

  @Column(name = "tax_rate_percent", precision = 5, scale = 2, nullable = false)
  private java.math.BigDecimal taxRatePercent;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  // M:N sin campos extra usando join table product_tag
  @ManyToMany
  @JoinTable(
      name = "product_tag",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"),
      uniqueConstraints =
          @UniqueConstraint(
              name = "uk_product_tag",
              columnNames = {"product_id", "tag_id"}))
  @Builder.Default
  private Set<Tag> tags = new HashSet<>();
}
