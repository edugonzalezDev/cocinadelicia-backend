package com.cocinadelicia.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request para actualizar el perfil básico de un usuario (Admin).
 *
 * <p>Todos los campos son opcionales. Solo los campos no-null serán actualizados.
 */
@Schema(description = "Datos para actualizar el perfil de un usuario")
public record UpdateUserProfileRequest(
    @Schema(description = "Nombre del usuario", example = "Juan")
        @Size(max = 191, message = "El nombre no puede exceder 191 caracteres")
        String firstName,
    @Schema(description = "Apellido del usuario", example = "Pérez")
        @Size(max = 191, message = "El apellido no puede exceder 191 caracteres")
        String lastName,
    @Schema(description = "Teléfono del usuario", example = "+59899123456")
        @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
        String phone) {}
