package com.cocinadelicia.backend.user.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "customer_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE customer_address SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CustomerAddress extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @Column(length = 100)
  private String label;

  @Column(name = "line1", length = 191, nullable = false)
  private String line1;

  @Column(name = "line2", length = 191)
  private String line2;

  @Column(length = 100, nullable = false)
  private String city;

  @Column(length = 100)
  private String region;

  @Column(name = "postal_code", length = 20)
  private String postalCode;

  @Column(length = 191)
  private String reference;
}
