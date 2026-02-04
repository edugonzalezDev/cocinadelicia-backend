package com.cocinadelicia.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * DTO para exponer logs de auditoría de usuarios.
 */
@Schema(description = "Registro de auditoría de acción sobre usuario")
public record UserAuditLogDTO(
    @Schema(description = "ID del registro de auditoría", example = "1") Long id,
    @Schema(description = "ID del usuario afectado", example = "5") Long userId,
    @Schema(
            description = "Acción realizada",
            example = "ROLE_CHANGED",
            allowableValues = {
              "USER_INVITED",
              "USER_IMPORTED",
              "ROLE_CHANGED",
              "STATUS_CHANGED",
              "USER_SYNCED"
            })
        String action,
    @Schema(
            description = "Email del administrador que realizó la acción",
            example = "admin@example.com")
        String changedBy,
    @Schema(description = "Fecha y hora de la acción", example = "2026-02-04T12:30:00Z")
        Instant changedAt,
    @Schema(
            description = "Detalles adicionales de la acción en formato JSON",
            example = "{\"oldRoles\":[\"CUSTOMER\"],\"newRoles\":[\"CUSTOMER\",\"ADMIN\"]}")
        String details) {}
