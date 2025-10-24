package com.cocinadelicia.backend.user.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(
    name = "user_role",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_user_role",
            columnNames = {"user_id", "role_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

  @EmbeddedId private UserRoleId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private AppUser user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @Column(name = "assigned_at", nullable = false)
  private Instant assignedAt;

  @PrePersist
  void onCreate() {
    if (assignedAt == null) assignedAt = Instant.now();
  }
}
