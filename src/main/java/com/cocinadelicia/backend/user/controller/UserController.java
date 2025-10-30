package com.cocinadelicia.backend.user.controller;

import com.cocinadelicia.backend.user.dto.UserRegistrationDTO;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.OK) // 200 idempotente
  public UserResponseDTO register(
      @RequestBody(required = false) UserRegistrationDTO body, JwtAuthenticationToken auth) {
    log.info("Register or update user called with body: {} and auth: {}", body, auth);
    return userService.registerOrUpdateFromToken(body, auth);
  }
}
