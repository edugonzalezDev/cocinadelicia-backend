package com.cocinadelicia.backend.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para asignar chef a una orden")
public record AssignChefRequest(
    @Schema(description = "Email del chef", example = "chef@example.com", required = true)
        @NotBlank(message = "El email del chef es obligatorio") @Email(message = "Debe ser un email válido") String chefEmail) {}
