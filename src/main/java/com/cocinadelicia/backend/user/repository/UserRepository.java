package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByCognitoUserId(String cognitoUserId);
    Optional<AppUser> findByEmail(String email);
}
