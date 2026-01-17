package com.cocinadelicia.backend.user.service;

import com.cocinadelicia.backend.user.dto.UserRegistrationDTO;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface UserService {

  UserResponseDTO registerOrUpdateFromToken(UserRegistrationDTO body, JwtAuthenticationToken auth);

  // 409
  class EmailConflictException extends RuntimeException {
    public EmailConflictException(String email) {
      super("Email already in use: " + email);
    }
  }

  Long resolveUserIdFromJwt(Jwt jwt);

  // 400
  class MissingEmailException extends RuntimeException {
    public MissingEmailException(String msg) {
      super(msg);
    }
  }
}
