package com.cocinadelicia.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * DTO para item de usuario en listado Admin.
 *
 * <p>Incluye información básica del usuario, roles asignados y si tiene pedidos pendientes.
 */
@Schema(description = "Item de usuario para listado Admin")
public record AdminUserListItemDTO(
    @Schema(description = "ID del usuario", example = "1") Long id,
    @Schema(description = "ID del usuario en Cognito (sub claim)", example = "abc123-...")
        String cognitoUserId,
    @Schema(description = "Email del usuario", example = "juan.perez@example.com") String email,
    @Schema(description = "Nombre", example = "Juan") String firstName,
    @Schema(description = "Apellido", example = "Pérez") String lastName,
    @Schema(description = "Teléfono", example = "+59899123456") String phone,
    @Schema(description = "Usuario activo/inactivo", example = "true") Boolean isActive,
    @Schema(description = "Roles asignados", example = "[\"CUSTOMER\", \"CHEF\"]")
        Set<String> roles,
    @Schema(
            description = "Tiene pedidos pendientes (status != DELIVERED/CANCELLED)",
            example = "true")
        Boolean hasPendingOrders) {}
