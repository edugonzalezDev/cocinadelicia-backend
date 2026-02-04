package com.cocinadelicia.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para importar un usuario existente de Cognito a la DB local.
 *
 * <p>Solo requiere el email del usuario. Los demás datos (nombre, teléfono, roles) se sincronizan
 * automáticamente desde Cognito.
 */
@Schema(description = "Request para importar usuario existente de Cognito (Admin)")
public record ImportUserRequest(
    @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        @Schema(
            description = "Email del usuario existente en Cognito (usado como username)",
            example = "usuario.existente@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String email) {}
