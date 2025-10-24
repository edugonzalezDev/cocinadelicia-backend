package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

interface RoleRepository extends JpaRepository<Role, Long> {}
