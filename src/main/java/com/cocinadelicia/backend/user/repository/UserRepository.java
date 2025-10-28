package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByCognitoUserId(String cognitoUserId);

  Optional<AppUser> findByEmail(String email);
}
