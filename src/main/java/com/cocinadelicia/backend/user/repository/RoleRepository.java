package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);

    // Buscar todos los roles cuyo name ∈ {ADMIN, CHEF, ...}
    List<Role> findByNameIn(Collection<RoleName> names);
}
