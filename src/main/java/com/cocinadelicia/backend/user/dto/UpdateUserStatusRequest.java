package com.cocinadelicia.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request para actualizar el estado de activación de un usuario (US06).
 *
 * <p>Controla si el usuario puede acceder al sistema (enable/disable en Cognito).
 */
@Schema(description = "Request para activar o desactivar un usuario")
public record UpdateUserStatusRequest(
    @Schema(
            description =
                "Estado de activación. true=activo (puede acceder), false=inactivo (bloqueado)",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El estado de activación es obligatorio")
        Boolean isActive) {}
