// src/main/java/com/cocinadelicia/backend/user/service/impl/CurrentUserServiceImpl.java
package com.cocinadelicia.backend.user.service.impl;

import com.cocinadelicia.backend.user.dto.UserRegistrationDTO;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import com.cocinadelicia.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

  private final UserService userService;

  @Override
  public Long getOrCreateCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
      throw new IllegalStateException("No JWT authentication present.");
    }
    // Reutiliza tu lógica centralizada (crea o actualiza y nos devuelve el id)
    var dto = userService.registerOrUpdateFromToken((UserRegistrationDTO) null, jwtAuth);
    return dto.getId();
  }
}
