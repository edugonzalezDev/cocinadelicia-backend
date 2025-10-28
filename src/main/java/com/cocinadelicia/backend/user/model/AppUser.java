package com.cocinadelicia.backend.user.model;

import com.cocinadelicia.backend.common.model.BaseAudit;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "app_user",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_user_cognito", columnNames = "cognito_user_id"),
      @UniqueConstraint(name = "uk_user_email", columnNames = "email")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE app_user SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class AppUser extends BaseAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cognito_user_id", length = 191, nullable = false)
  private String cognitoUserId;

  @Column(name = "first_name", length = 191)
  private String firstName;

  @Column(name = "last_name", length = 191)
  private String lastName;

  @Column(length = 191, nullable = false)
  private String email;

  @Column(length = 50)
  private String phone;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  // Conveniencia para navegar roles
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<UserRole> roles = new HashSet<>();
}
