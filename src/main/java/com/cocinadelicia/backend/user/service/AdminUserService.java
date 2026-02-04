package com.cocinadelicia.backend.user.service;

import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UpdateUserProfileRequest;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import java.util.Set;
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

  /**
   * Importa un usuario existente de Cognito a la DB local.
   *
   * <p>Sincroniza datos de perfil y roles desde Cognito.
   *
   * @param request datos del usuario a importar (email)
   * @return información del usuario importado
   * @throws com.cocinadelicia.backend.common.exception.NotFoundException si el usuario no existe
   *     en Cognito
   * @throws com.cocinadelicia.backend.common.exception.ConflictException si el usuario ya está
   *     importado o hay conflicto de email
   */
  UserResponseDTO importUser(ImportUserRequest request);

  /**
   * Actualiza el perfil básico de un usuario en DB.
   *
   * <p>Solo actualiza los campos no-null del request (firstName, lastName, phone).
   *
   * @param userId ID del usuario a actualizar
   * @param request datos de perfil a actualizar
   * @return información actualizada del usuario
   * @throws com.cocinadelicia.backend.common.exception.NotFoundException si el usuario no existe
   */
  UserResponseDTO updateUserProfile(Long userId, UpdateUserProfileRequest request);

  /**
   * Actualiza los roles de un usuario (set completo, no incremental) - US05.
   *
   * <p>Sincroniza cambios con Cognito (add/remove user from groups).
   *
   * <p><strong>Hardening:</strong>
   *
   * <ul>
   *   <li>Previene auto-democión: un admin no puede quitarse a sí mismo el rol ADMIN
   *   <li>Requiere confirmación explícita para promover a ADMIN (confirmText debe coincidir)
   * </ul>
   *
   * @param userId ID del usuario a actualizar
   * @param roles conjunto completo de roles a asignar
   * @param confirmText texto de confirmación (obligatorio solo si se agrega ADMIN)
   * @param performedBy email del usuario que realiza la operación (para validar auto-democión)
   * @return información actualizada del usuario
   * @throws com.cocinadelicia.backend.common.exception.NotFoundException si el usuario no existe
   * @throws com.cocinadelicia.backend.common.exception.BadRequestException si falla validación de
   *     hardening
   */
  UserResponseDTO updateRoles(
      Long userId, Set<RoleName> roles, String confirmText, String performedBy);

  /**
   * Actualiza el estado de activación de un usuario (activo/inactivo) - US06.
   *
   * <p>Sincroniza cambios con Cognito (enable/disable user).
   *
   * <p><strong>Cognito primero:</strong> el cambio se aplica primero en Cognito (fuente de verdad
   * para acceso), luego se espeja en DB.
   *
   * @param userId ID del usuario a actualizar
   * @param isActive true para activar (permitir acceso), false para desactivar (bloquear acceso)
   * @param performedBy email del usuario que realiza la operación (para auditoría)
   * @return información actualizada del usuario
   * @throws com.cocinadelicia.backend.common.exception.NotFoundException si el usuario no existe
   */
  UserResponseDTO updateStatus(Long userId, boolean isActive, String performedBy);
}
