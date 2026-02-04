package com.cocinadelicia.backend.user.dto;

import com.cocinadelicia.backend.common.model.enums.RoleName;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Set;

/**
 * Filtros para listado de usuarios en Admin.
 *
 * <p>Todos los campos son opcionales. Si no se especifican, no se aplican filtros.
 */
public record AdminUserFilter(
    @Parameter(description = "Búsqueda en email, nombre, apellido o teléfono (case-insensitive)")
        String q,
    @Parameter(
            description = "Filtrar por roles (OR lógico). Ej: ADMIN,CHEF",
            example = "ADMIN,CHEF")
        Set<RoleName> roles,
    @Parameter(description = "Filtrar por estado activo/inactivo", example = "true")
        Boolean isActive,
    @Parameter(
            description = "Filtrar usuarios con pedidos pendientes (status != DELIVERED/CANCELLED)",
            example = "true")
        Boolean hasPendingOrders) {}
