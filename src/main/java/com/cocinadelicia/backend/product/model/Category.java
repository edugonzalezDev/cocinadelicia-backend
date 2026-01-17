package com.cocinadelicia.backend.product.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
  name = "category",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_category_name", columnNames = "name"),
    @UniqueConstraint(name = "uk_category_slug", columnNames = "slug")
  })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE category SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Category extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 191, nullable = false)
  private String name;

  @Column(length = 191, nullable = false)
  private String slug;

  @Column(columnDefinition = "text")
  private String description;
}
