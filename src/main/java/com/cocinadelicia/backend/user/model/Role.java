package com.cocinadelicia.backend.user.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "role",
    uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE role SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Role extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private RoleName name;

  @Column(length = 255)
  private String description;
}
