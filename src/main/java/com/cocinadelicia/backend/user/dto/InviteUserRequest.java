package com.cocinadelicia.backend.user.dto;

import com.cocinadelicia.backend.common.model.enums.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

/**
 * Request para invitar un usuario nuevo desde Admin.
 *
 * <p>El usuario será creado en Cognito y persistido en DB con los roles especificados.
 */
@Schema(description = "Request para invitar usuario nuevo (Admin)")
public record InviteUserRequest(
    @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        @Schema(
            description = "Email del usuario (usado como username en Cognito)",
            example = "juan.perez@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
    @Schema(description = "Nombre del usuario", example = "Juan") String firstName,
    @Schema(description = "Apellido del usuario", example = "Pérez") String lastName,
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Teléfono inválido (8-15 dígitos, opcional +)")
        @Schema(
            description = "Teléfono del usuario (formato internacional recomendado)",
            example = "+59899123456")
        String phone,
    @NotEmpty(message = "Debe especificar al menos un rol")
        @Schema(
            description = "Roles a asignar al usuario",
            example = "[\"CUSTOMER\", \"CHEF\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Set<RoleName> roles) {}
