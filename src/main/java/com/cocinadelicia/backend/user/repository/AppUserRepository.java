package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

interface AppUserRepository extends JpaRepository<AppUser, Long> {}
