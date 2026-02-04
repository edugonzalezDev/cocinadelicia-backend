package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
  Optional<AppUser> findByCognitoUserId(String cognitoUserId);

  Optional<AppUser> findByEmail(String email);
}
