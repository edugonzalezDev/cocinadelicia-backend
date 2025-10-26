package com.cocinadelicia.backend.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
class AdminPingController {

  @GetMapping("/ping")
  @PreAuthorize("hasRole('ADMIN')")
  public String pingAdmin() {
    return "pong-admin";
  }
}
