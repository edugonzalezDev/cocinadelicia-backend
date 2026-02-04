package com.cocinadelicia.backend.user.dto;

import com.cocinadelicia.backend.common.model.enums.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * Request para actualizar roles de un usuario (US05).
 *
 * <p>El conjunto de roles es reemplazado completamente (no incremental).
 *
 * <p>Si se agrega el rol ADMIN, se requiere confirmación explícita via confirmText.
 */
@Schema(description = "Request para actualizar roles de un usuario")
public record UpdateRolesRequest(
    @Schema(
            description = "Conjunto completo de roles a asignar",
            example = "[\"CUSTOMER\", \"CHEF\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Los roles son obligatorios")
        @NotEmpty(message = "Debe especificar al menos un rol")
        Set<RoleName> roles,
    @Schema(
            description =
                "Texto de confirmación requerido solo si se agrega ADMIN. Debe coincidir exactamente con 'PROMOVER {email} A ADMIN'",
            example = "PROMOVER juan.perez@example.com A ADMIN",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String confirmText) {}
