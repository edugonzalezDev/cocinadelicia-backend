package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
