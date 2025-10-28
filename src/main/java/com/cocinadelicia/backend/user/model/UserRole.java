package com.cocinadelicia.backend.user.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(
        name = "user_role",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_role",
                columnNames = {"user_id", "role_id"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    /** Conveniencia: permite new UserRole(user, role) */
    public UserRole(AppUser user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(
                user != null ? user.getId() : null,
                role != null ? role.getId() : null
        );
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = new UserRoleId(
                    user != null ? user.getId() : null,
                    role != null ? role.getId() : null
            );
        }
        if (this.assignedAt == null) {
            this.assignedAt = Instant.now();
        }
    }
}
