package com.cocinadelicia.backend.user.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
  // Opcionales (el backend tomará sub/email de JWT)
  private String firstName;
  private String lastName;
  private String phone;
}
