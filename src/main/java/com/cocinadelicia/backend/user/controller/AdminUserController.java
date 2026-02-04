package com.cocinadelicia.backend.user.controller;

import com.cocinadelicia.backend.common.web.ApiError;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UpdateUserProfileRequest;
import com.cocinadelicia.backend.user.dto.UserAuditLogDTO;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "admin-users", description = "Gestión de usuarios (Admin)")
@SecurityRequirement(name = "bearer-jwt")
@Log4j2
public class AdminUserController {

  private final AdminUserService adminUserService;

  @Operation(
      summary = "Listar usuarios (Admin)",
      description =
          """
          Lista paginada de usuarios con búsqueda y filtros.

          **Filtros disponibles:**
          - `q`: búsqueda en email, nombre, apellido o teléfono (case-insensitive)
          - `roles`: filtrar por uno o más roles (OR lógico). Ej: ADMIN,CHEF
          - `isActive`: filtrar por estado activo/inactivo
          - `hasPendingOrders`: usuarios con/sin pedidos pendientes (status != DELIVERED/CANCELLED)

          **Paginación:**
          - `page`: número de página (0-based, default: 0)
          - `size`: tamaño de página (default: 20, max: 100)
          - `sort`: ordenamiento (ej: email,asc o createdAt,desc)

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<AdminUserListItemDTO>> listUsers(
      @ParameterObject AdminUserFilter filter,
      @ParameterObject
          @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    log.info(
        "AdminUserController.listUsers called with filters={} page={} size={}",
        filter,
        pageable.getPageNumber(),
        pageable.getPageSize());

    // Aplicar límite máximo de tamaño de página
    Pageable safePageable = safePageable(pageable, 100);

    PageResponse<AdminUserListItemDTO> response = adminUserService.listUsers(filter, safePageable);

    log.debug(
        "AdminUserController.listUsers returned {} users (total: {})",
        response.content().size(),
        response.totalElements());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Invitar usuario nuevo (Admin)",
      description =
          """
          Crea un usuario nuevo en Cognito y lo persiste en DB con los roles especificados.

          El usuario recibirá un email de invitación con credenciales temporales para configurar su contraseña.

          **Campos requeridos:**
          - `email`: Email válido (usado como username en Cognito)
          - `roles`: Al menos un rol (ADMIN, CHEF, COURIER, CUSTOMER)

          **Campos opcionales:**
          - `firstName`, `lastName`, `phone`

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario invitado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Validación fallida (email inválido, rol vacío, etc.)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Email ya existe en DB o Cognito",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PostMapping("/invite")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> inviteUser(@Valid @RequestBody InviteUserRequest request) {

    log.info("AdminUserController.inviteUser called with email={}", request.email());

    UserResponseDTO response = adminUserService.inviteUser(request);

    log.info("User invited successfully: {} (id={})", response.getEmail(), response.getId());

    return ResponseEntity.status(201).body(response);
  }

  @Operation(
      summary = "Importar usuario existente de Cognito (Admin)",
      description =
          """
          Importa a la DB local un usuario que ya existe en Cognito.

          Sincroniza datos de perfil (nombre, apellido, teléfono) y roles (grupos) desde Cognito.

          **Campo requerido:**
          - `email`: Email del usuario en Cognito (usado como username)

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario importado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Validación fallida (email inválido o vacío)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no existe en Cognito",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Usuario ya importado o conflicto de email",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PostMapping("/import")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> importUser(@Valid @RequestBody ImportUserRequest request) {

    log.info("AdminUserController.importUser called with email={}", request.email());

    UserResponseDTO response = adminUserService.importUser(request);

    log.info("User imported successfully: {} (id={})", response.getEmail(), response.getId());

    return ResponseEntity.status(201).body(response);
  }

  @Operation(
      summary = "Actualizar perfil de usuario (Admin)",
      description =
          """
          Actualiza el perfil básico de un usuario en DB (firstName, lastName, phone).

          Solo se actualizan los campos enviados (no-null). Los campos omitidos permanecen sin cambios.

          **Nota:** El email NO es editable desde este endpoint.

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil actualizado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Validación fallida (campo muy largo, formato inválido, etc.)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> updateUserProfile(
      @PathVariable Long id, @Valid @RequestBody UpdateUserProfileRequest request) {

    log.info("AdminUserController.updateUserProfile called for userId={}", id);

    UserResponseDTO response = adminUserService.updateUserProfile(id, request);

    log.info("User profile updated successfully for userId={}", id);

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Actualizar roles de usuario (Admin) - US05",
      description =
          """
          Actualiza los roles de un usuario (reemplazo completo, no incremental).

          Los cambios se sincronizan automáticamente con Cognito (add/remove user from groups).

          **Hardening:**
          - Un admin NO puede quitarse a sí mismo el rol ADMIN (auto-democión bloqueada)
          - Promover a ADMIN requiere confirmación explícita via `confirmText`

          **Campo requerido:**
          - `roles`: Conjunto completo de roles a asignar (Set<RoleName>)

          **Campo condicional:**
          - `confirmText`: Obligatorio SOLO si se agrega el rol ADMIN. Debe coincidir exactamente con:
            `"PROMOVER {email_del_usuario} A ADMIN"` (en mayúsculas)

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Roles actualizados exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description =
                "Validación fallida (auto-democión, confirmación inválida, conjunto vacío, etc.)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PutMapping("/{id}/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> updateRoles(
      @PathVariable Long id,
      @Valid @RequestBody com.cocinadelicia.backend.user.dto.UpdateRolesRequest request,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          org.springframework.security.oauth2.jwt.Jwt jwt) {

    String performedBy = extractEmailFromJwt(jwt);
    log.info(
        "AdminUserController.updateRoles called for userId={} newRoles={} by={}",
        id,
        request.roles(),
        performedBy);

    UserResponseDTO response =
        adminUserService.updateRoles(id, request.roles(), request.confirmText(), performedBy);

    log.info("Roles updated successfully for userId={}", id);

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Actualizar estado de activación de usuario (Admin) - US06",
      description =
          """
          Activa o desactiva un usuario (enable/disable en Cognito + espejo en DB).

          **Efecto:**
          - `isActive=true`: El usuario puede acceder al sistema (AdminEnableUser en Cognito)
          - `isActive=false`: El usuario NO puede acceder (AdminDisableUser en Cognito, bloqueo efectivo)

          **Sincronización:**
          - Cambio se aplica primero en Cognito (fuente de verdad para acceso)
          - Luego se espeja en `app_user.is_active` (DB)

          **Campo requerido:**
          - `isActive`: Boolean (true=activo, false=inactivo)

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Validación fallida (isActive null)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest request,
      @org.springframework.security.core.annotation.AuthenticationPrincipal
          org.springframework.security.oauth2.jwt.Jwt jwt) {

    String performedBy = extractEmailFromJwt(jwt);
    log.info(
        "AdminUserController.updateStatus called for userId={} isActive={} by={}",
        id,
        request.isActive(),
        performedBy);

    UserResponseDTO response = adminUserService.updateStatus(id, request.isActive(), performedBy);

    log.info("Status updated successfully for userId={}", id);

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Sincronizar usuario desde Cognito (Admin) - US07",
      description =
          """
          Sincroniza roles del usuario desde Cognito groups a DB.

          **Proceso:**
          1. Obtiene roles actuales del usuario en Cognito
          2. Compara con roles en DB
          3. Actualiza DB para reflejar estado de Cognito
          4. Registra acción en auditoría

          **Casos de uso:**
          - Reconciliar diferencias tras cambios manuales en Cognito
          - Verificar sincronización tras importación
          - Troubleshooting de permisos

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario sincronizado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado en DB o Cognito",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @PostMapping("/{id}/sync")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> syncUser(@PathVariable Long id) {

    log.info("AdminUserController.syncUser called for userId={}", id);

    UserResponseDTO response = adminUserService.syncUser(id);

    log.info("User synced successfully: userId={}", id);

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Obtener historial de auditoría de usuario (Admin) - US07",
      description =
          """
          Devuelve historial paginado de acciones realizadas sobre un usuario.

          **Acciones registradas:**
          - USER_INVITED: Usuario invitado y creado en Cognito
          - USER_IMPORTED: Usuario importado desde Cognito
          - ROLE_CHANGED: Cambio de roles
          - STATUS_CHANGED: Activación/desactivación
          - USER_SYNCED: Sincronización manual desde Cognito

          **Paginación:**
          - Ordenado por fecha descendente (más reciente primero)
          - Tamaño de página default: 20
          - Máximo: 100

          **Requiere:** Rol ADMIN
          """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Historial de auditoría obtenido exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Sin permisos (requiere rol ADMIN)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)))
      })
  @GetMapping("/{id}/audit")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<UserAuditLogDTO>> getUserAuditLog(
      @PathVariable Long id,
      @ParameterObject
          @PageableDefault(size = 20, sort = "changedAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    log.info(
        "AdminUserController.getUserAuditLog called for userId={} page={} size={}",
        id,
        pageable.getPageNumber(),
        pageable.getPageSize());

    // Aplicar límite máximo de tamaño de página
    Pageable safePageable = safePageable(pageable, 100);

    PageResponse<UserAuditLogDTO> response = adminUserService.getUserAuditLog(id, safePageable);

    log.debug(
        "AdminUserController.getUserAuditLog returned {} logs (total: {})",
        response.content().size(),
        response.totalElements());

    return ResponseEntity.ok(response);
  }

  /**
   * Limita el tamaño de página al máximo configurado para evitar queries excesivas.
   *
   * @param pageable configuración original de paginación
   * @param maxSize tamaño máximo permitido
   * @return Pageable con size ajustado si excede el máximo
   */
  private Pageable safePageable(Pageable pageable, int maxSize) {
    int size = Math.min(pageable.getPageSize(), maxSize);
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  /**
   * Extrae el email del JWT para auditoría.
   *
   * <p>Intenta obtener el email en el siguiente orden:
   *
   * <ol>
   *   <li>Claim "email"
   *   <li>Claim "preferred_username"
   *   <li>Claim "sub" (último recurso)
   * </ol>
   *
   * @param jwt token JWT
   * @return email o identificador del usuario, nunca null
   */
  private String extractEmailFromJwt(org.springframework.security.oauth2.jwt.Jwt jwt) {
    String email = jwt.getClaim("email");
    if (email != null && !email.isBlank()) {
      return email;
    }

    String preferredUsername = jwt.getClaim("preferred_username");
    if (preferredUsername != null && !preferredUsername.isBlank()) {
      log.warn("JWT missing 'email' claim, using 'preferred_username': {}", preferredUsername);
      return preferredUsername;
    }

    String sub = jwt.getSubject();
    if (sub != null && !sub.isBlank()) {
      log.warn("JWT missing 'email' and 'preferred_username' claims, using 'sub': {}", sub);
      return sub;
    }

    log.error("JWT missing all expected identity claims (email, preferred_username, sub)");
    return "UNKNOWN_JWT_USER";
  }
}
