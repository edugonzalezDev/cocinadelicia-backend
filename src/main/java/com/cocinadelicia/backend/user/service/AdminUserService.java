package com.cocinadelicia.backend.user.service;

import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import org.springframework.data.domain.Pageable;

/** Servicio de gestión de usuarios para Admin. */
public interface AdminUserService {

  /**
   * Lista usuarios con búsqueda, filtros y paginación.
   *
   * @param filter filtros opcionales (búsqueda, roles, estado, pedidos pendientes)
   * @param pageable configuración de paginación y ordenamiento
   * @return página de usuarios con metadata de paginación
   */
  PageResponse<AdminUserListItemDTO> listUsers(AdminUserFilter filter, Pageable pageable);

  /**
   * Invita un usuario nuevo creándolo en Cognito y persistiéndolo en DB.
   *
   * <p>El usuario recibirá un email de invitación con credenciales temporales.
   *
   * @param request datos del usuario a invitar (email, nombre, roles, etc.)
   * @return información del usuario creado
   * @throws com.cocinadelicia.backend.common.exception.ConflictException si el email ya existe en
   *     DB o Cognito
   * @throws com.cocinadelicia.backend.common.exception.BadRequestException si hay errores de
   *     validación
   */
  UserResponseDTO inviteUser(InviteUserRequest request);
}
