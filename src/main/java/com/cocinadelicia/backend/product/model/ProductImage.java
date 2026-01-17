package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
  name = "product_image",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_product_image_product_key",
      columnNames = {"product_id", "object_key"})
  })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product_image SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ProductImage extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  /**
   * Clave estable del objeto en S3, ej: products/{productId}/{uuid}.webp
   * No guardamos la URL completa para poder cambiar a CloudFront sin tocar la BD.
   */
  @Column(name = "object_key", length = 512, nullable = false)
  private String objectKey;

  /** Marca si es la imagen principal del producto. */
  @Column(name = "is_main", nullable = false)
  @Builder.Default
  private boolean isMain = false;

  /** Orden visual dentro de la galería. */
  @Column(name = "sort_order", nullable = false)
  @Builder.Default
  private int sortOrder = 0;
}
