// src/main/java/com/cocinadelicia/backend/user/controller/UserController.java
package com.cocinadelicia.backend.user.controller;

import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "users", description = "Perfil del usuario autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  @Operation(summary = "Devuelve el perfil app_user del usuario autenticado (crea si no existe)")
  public UserResponseDTO me(JwtAuthenticationToken auth) {
    // Reutiliza tu registro/actualización centralizado
    return userService.registerOrUpdateFromToken(null, auth);
  }
}
