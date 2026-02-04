package com.cocinadelicia.backend.user.controller;

import com.cocinadelicia.backend.common.web.ApiError;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
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
}
