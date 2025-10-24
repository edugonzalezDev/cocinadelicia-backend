package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {}
