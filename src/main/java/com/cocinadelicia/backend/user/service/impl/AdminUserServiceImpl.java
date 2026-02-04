package com.cocinadelicia.backend.user.service.impl;

import static com.cocinadelicia.backend.user.repository.spec.UserSpecifications.*;

import com.cocinadelicia.backend.auth.cognito.CognitoAdminClient;
import com.cocinadelicia.backend.auth.cognito.CognitoUserInfo;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.ConflictException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UpdateUserProfileRequest;
import com.cocinadelicia.backend.user.dto.UserAuditLogDTO;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserAuditLog;
import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserAuditLogRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.service.AdminUserService;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

  private final UserRepository userRepository;
  private final CustomerOrderRepository customerOrderRepository;
  private final CognitoAdminClient cognitoAdminClient;
  private final RoleRepository roleRepository;
  private final UserAuditLogRepository userAuditLogRepository;

  @Override
  public PageResponse<AdminUserListItemDTO> listUsers(AdminUserFilter filter, Pageable pageable) {
    log.info(
        "AdminUserService.listUsers filters=[q={}, roles={}, isActive={}, hasPendingOrders={}] page={} size={}",
        filter.q(),
        filter.roles(),
        filter.isActive(),
        filter.hasPendingOrders(),
        pageable.getPageNumber(),
        pageable.getPageSize());

    // 1. Construir Specification combinando filtros
    Specification<AppUser> spec =
        Specification.allOf(
            searchByKeyword(filter.q()),
            hasRolesIn(filter.roles()),
            isActive(filter.isActive()),
            hasPendingOrders(filter.hasPendingOrders()));

    // 2. Ejecutar query con paginación
    Page<AppUser> page = userRepository.findAll(spec, pageable);

    // 3. Calcular hasPendingOrders para usuarios de la página actual
    List<Long> userIds = page.getContent().stream().map(AppUser::getId).toList();
    Set<Long> usersWithPendingOrders = getUsersWithPendingOrders(userIds);

    // 4. Mapear a DTO
    List<AdminUserListItemDTO> content =
        page.getContent().stream().map(user -> mapToDTO(user, usersWithPendingOrders)).toList();

    log.debug("AdminUserService.listUsers returned {} users", content.size());

    return new PageResponse<>(
        content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  @Override
  @Transactional
  public UserResponseDTO inviteUser(InviteUserRequest request) {
    log.info(
        "AdminUserService.inviteUser: inviting user with email={} roles={}",
        request.email(),
        request.roles());

    // 1. Normalizar email
    String normalizedEmail = normalizeEmail(request.email());
    log.debug("Email normalized: {} -> {}", request.email(), normalizedEmail);

    // 2. Validar que no exista en DB
    if (userRepository.findByEmail(normalizedEmail).isPresent()) {
      log.warn("User with email {} already exists in DB", normalizedEmail);
      throw new ConflictException(
          "EMAIL_CONFLICT", "El email ya está registrado en la base de datos.");
    }

    // 3. Crear usuario en Cognito (puede lanzar ConflictException si existe en Cognito)
    String cognitoUserId =
        cognitoAdminClient.createUser(
            normalizedEmail, request.firstName(), request.lastName(), request.phone());
    log.info("User created in Cognito with sub: {}", cognitoUserId);

    // 4. Asignar roles en Cognito (grupos)
    for (RoleName roleName : request.roles()) {
      try {
        cognitoAdminClient.addUserToGroup(normalizedEmail, roleName.name());
        log.debug("User {} added to Cognito group: {}", normalizedEmail, roleName);
      } catch (Exception e) {
        log.error(
            "Failed to add user {} to Cognito group {}: {}",
            normalizedEmail,
            roleName,
            e.getMessage());
        // Continuamos con los otros roles, sync posterior reconciliará
      }
    }

    // 5. Persistir en DB
    AppUser newUser =
        AppUser.builder()
            .cognitoUserId(cognitoUserId)
            .email(normalizedEmail)
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phone(request.phone())
            .isActive(true)
            .roles(new LinkedHashSet<>())
            .build();

    newUser = userRepository.save(newUser);
    log.debug("User persisted in DB with id: {}", newUser.getId());

    // 6. Asignar roles en DB
    assignRolesToUser(newUser, request.roles());
    newUser = userRepository.save(newUser);
    log.info("User {} invited successfully with {} roles", normalizedEmail, request.roles().size());

    // 7. Registrar auditoría persistente - US07
    String auditDetails =
        String.format("{\"email\":\"%s\",\"roles\":%s}", normalizedEmail, toJsonArray(request.roles()));
    logAuditAction(newUser.getId(), "USER_INVITED", "ADMIN", auditDetails);

    // 8. Mapear a DTO y retornar
    return mapToUserResponseDTO(newUser);
  }

  @Override
  @Transactional
  public UserResponseDTO importUser(ImportUserRequest request) {
    log.info("AdminUserService.importUser: importing user with email={}", request.email());

    // 1. Normalizar email
    String normalizedEmail = normalizeEmail(request.email());
    log.debug("Email normalized: {} -> {}", request.email(), normalizedEmail);

    // 2. Obtener usuario de Cognito (lanza NotFoundException si no existe)
    CognitoUserInfo cognitoUser = cognitoAdminClient.getUser(normalizedEmail);
    log.info(
        "User found in Cognito: {} (sub={})", normalizedEmail, cognitoUser.getCognitoUserId());

    // 3. Validar que no exista en DB con ese cognitoUserId
    if (userRepository.findByCognitoUserId(cognitoUser.getCognitoUserId()).isPresent()) {
      log.warn("User with cognitoUserId {} already exists in DB", cognitoUser.getCognitoUserId());
      throw new ConflictException(
          "USER_ALREADY_IMPORTED", "El usuario ya está importado en la base de datos.");
    }

    // 4. Validar que no exista en DB con ese email (otro sub)
    if (userRepository.findByEmail(normalizedEmail).isPresent()) {
      log.warn(
          "User with email {} already exists in DB with different cognitoUserId", normalizedEmail);
      throw new ConflictException(
          "EMAIL_CONFLICT", "Ya existe un usuario con ese email en la base de datos.");
    }

    // 5. Crear usuario en DB con datos de Cognito
    // Aplicar valores por defecto si firstName/lastName no existen en Cognito
    String firstName = cognitoUser.getFirstName();
    String lastName = cognitoUser.getLastName();

    if (firstName == null || firstName.isBlank()) {
      // Extraer nombre del email como fallback (parte antes del @)
      String emailLocal = normalizedEmail.contains("@")
          ? normalizedEmail.substring(0, normalizedEmail.indexOf("@"))
          : normalizedEmail;
      firstName = capitalize(emailLocal.replace(".", " ").replace("_", " "));
      log.debug("firstName not found in Cognito, using email-based default: {}", firstName);
    }

    if (lastName == null || lastName.isBlank()) {
      lastName = "(Importado)";
      log.debug("lastName not found in Cognito, using default: {}", lastName);
    }

    AppUser newUser =
        AppUser.builder()
            .cognitoUserId(cognitoUser.getCognitoUserId())
            .email(normalizedEmail)
            .firstName(firstName)
            .lastName(lastName)
            .phone(cognitoUser.getPhone())
            .isActive(true) // Por defecto activo al importar (US06 manejará cambios)
            .roles(new LinkedHashSet<>())
            .build();

    newUser = userRepository.save(newUser);
    log.debug("User persisted in DB with id: {}", newUser.getId());

    // 6. Sincronizar roles desde grupos de Cognito
    Set<RoleName> validRoles = mapCognitoGroupsToRoles(cognitoUser.getGroups());
    if (!validRoles.isEmpty()) {
      assignRolesToUser(newUser, validRoles);
      newUser = userRepository.save(newUser);
      log.info(
          "User {} imported with {} roles: {}", normalizedEmail, validRoles.size(), validRoles);
    } else {
      log.warn("User {} imported without roles (no valid groups in Cognito)", normalizedEmail);
    }

    // 7. Registrar auditoría persistente - US07
    String auditDetails =
        String.format(
            "{\"cognitoUserId\":\"%s\",\"email\":\"%s\",\"roles\":%s}",
            cognitoUser.getCognitoUserId(), normalizedEmail, toJsonArray(validRoles));
    logAuditAction(newUser.getId(), "USER_IMPORTED", "ADMIN", auditDetails);

    // 8. Mapear a DTO y retornar
    return mapToUserResponseDTO(newUser);
  }

  /**
   * Mapea grupos de Cognito a roles válidos del sistema.
   *
   * <p>Filtra solo grupos que coincidan con RoleName (ADMIN, CHEF, COURIER, CUSTOMER). Loguea
   * warning para grupos desconocidos.
   *
   * <p>Normaliza los grupos a uppercase ya que Cognito puede devolverlos en minúsculas.
   *
   * @param cognitoGroups grupos del usuario en Cognito
   * @return conjunto de roles válidos
   */
  private Set<RoleName> mapCognitoGroupsToRoles(Set<String> cognitoGroups) {
    if (cognitoGroups == null || cognitoGroups.isEmpty()) {
      return Set.of();
    }

    Set<String> validRoleNames =
        Arrays.stream(RoleName.values()).map(Enum::name).collect(Collectors.toSet());

    Set<RoleName> validRoles = new LinkedHashSet<>();
    Set<String> unknownGroups = new LinkedHashSet<>();

    for (String group : cognitoGroups) {
      // Normalizar a uppercase porque Cognito puede devolver grupos en minúsculas
      String normalizedGroup = group == null ? null : group.toUpperCase();

      if (normalizedGroup != null && validRoleNames.contains(normalizedGroup)) {
        validRoles.add(RoleName.valueOf(normalizedGroup));
      } else {
        unknownGroups.add(group); // Loguear el grupo original para debugging
      }
    }

    if (!unknownGroups.isEmpty()) {
      log.warn("Ignoring unknown Cognito groups (not mapped to RoleName): {}", unknownGroups);
    }

    log.debug(
        "Mapped {} Cognito groups to {} valid roles", cognitoGroups.size(), validRoles.size());
    return validRoles;
  }

  @Override
  @Transactional
  public UserResponseDTO updateUserProfile(Long userId, UpdateUserProfileRequest request) {
    log.info("AdminUserService.updateUserProfile: userId={} request={}", userId, request);

    // 1. Buscar usuario
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("User not found with id: {}", userId);
                  return new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado.");
                });

    // 2. Actualizar solo campos no-null
    boolean updated = false;

    if (request.firstName() != null) {
      user.setFirstName(request.firstName());
      updated = true;
      log.debug("Updated firstName for user {}", userId);
    }

    if (request.lastName() != null) {
      user.setLastName(request.lastName());
      updated = true;
      log.debug("Updated lastName for user {}", userId);
    }

    if (request.phone() != null) {
      user.setPhone(request.phone());
      updated = true;
      log.debug("Updated phone for user {}", userId);
    }

    // 3. Guardar si hubo cambios
    if (updated) {
      user = userRepository.save(user);
      log.info("User profile updated successfully for userId={}", userId);
    } else {
      log.debug("No fields to update for userId={}", userId);
    }

    // 4. Retornar DTO actualizado
    return mapToUserResponseDTO(user);
  }

  @Override
  @Transactional
  public UserResponseDTO updateRoles(
      Long userId, Set<RoleName> roles, String confirmText, String performedBy) {
    log.info(
        "AdminUserService.updateRoles: userId={} newRoles={} performedBy={}",
        userId,
        roles,
        performedBy);

    // 1. Buscar usuario
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("User not found with id: {}", userId);
                  return new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado.");
                });

    // 2. Capturar roles actuales (antes del cambio)
    Set<RoleName> currentRoles =
        user.getRoles().stream()
            .map(ur -> ur.getRole().getName())
            .collect(Collectors.toSet());

    log.debug("Current roles for user {}: {}", userId, currentRoles);

    // 3. Validación de hardening: auto-democión de ADMIN
    // Si el usuario que ejecuta la acción es el mismo que se está modificando
    if (performedBy != null && performedBy.equalsIgnoreCase(user.getEmail())) {
      boolean hadAdmin = currentRoles.contains(RoleName.ADMIN);
      boolean willHaveAdmin = roles.contains(RoleName.ADMIN);

      if (hadAdmin && !willHaveAdmin) {
        log.warn("Self-demotion attempt blocked: user {} tried to remove own ADMIN role", userId);
        throw new BadRequestException(
            "SELF_DEMOTION_NOT_ALLOWED",
            "No puede quitarse a sí mismo el rol ADMIN. Solicite a otro administrador que realice el cambio.");
      }
    }

    // 4. Validación de hardening: confirmación para promover a ADMIN
    boolean willAddAdmin = roles.contains(RoleName.ADMIN) && !currentRoles.contains(RoleName.ADMIN);
    if (willAddAdmin) {
      String expectedConfirmText = "PROMOVER " + user.getEmail().toUpperCase() + " A ADMIN";
      String normalizedConfirmText =
          confirmText == null ? null : confirmText.trim().toUpperCase();

      if (!expectedConfirmText.equals(normalizedConfirmText)) {
        log.warn(
            "Admin promotion blocked: invalid confirmText for user {}. Expected: '{}', Got: '{}'",
            userId,
            expectedConfirmText,
            confirmText);
        throw new BadRequestException(
            "ADMIN_PROMOTION_REQUIRES_CONFIRMATION",
            "Para promover a ADMIN debe confirmar escribiendo exactamente: '" + expectedConfirmText + "'");
      }
      log.info("Admin promotion confirmed for user {}", userId);
    }

    // 5. Calcular cambios en roles
    Set<RoleName> rolesToAdd = new java.util.HashSet<>(roles);
    rolesToAdd.removeAll(currentRoles);

    Set<RoleName> rolesToRemove = new java.util.HashSet<>(currentRoles);
    rolesToRemove.removeAll(roles);

    log.debug("Roles to add: {}", rolesToAdd);
    log.debug("Roles to remove: {}", rolesToRemove);

    // 6. Sincronizar con Cognito: grupos en minúsculas
    String username = user.getEmail();

    try {
      // Agregar nuevos grupos
      for (RoleName role : rolesToAdd) {
        String groupName = role.name().toLowerCase(); // admin, chef, courier, customer
        cognitoAdminClient.addUserToGroup(username, groupName);
        log.debug("Added user {} to Cognito group: {}", username, groupName);
      }

      // Remover grupos antiguos
      for (RoleName role : rolesToRemove) {
        String groupName = role.name().toLowerCase();
        cognitoAdminClient.removeUserFromGroup(username, groupName);
        log.debug("Removed user {} from Cognito group: {}", username, groupName);
      }

    } catch (Exception e) {
      log.error("Error syncing roles to Cognito for user {}: {}", userId, e.getMessage(), e);
      throw new RuntimeException(
          "Error al sincronizar roles con Cognito. Los cambios no se aplicaron en DB.", e);
    }

    // 7. Actualizar roles en DB
    // En lugar de clear() + add() (que causa ObjectDeletedException),
    // removemos solo los que ya no deben estar y agregamos solo los nuevos

    // Obtener entidades de roles a asignar
    List<Role> roleEntities = roleRepository.findByNameIn(roles);
    if (roleEntities.size() != roles.size()) {
      log.warn(
          "Some roles were not found in DB. Requested: {}, Found: {}",
          roles,
          roleEntities.stream().map(Role::getName).collect(Collectors.toSet()));
    }

    // Crear mapa de roles solicitados por nombre
    Map<RoleName, Role> rolesByName =
        roleEntities.stream().collect(Collectors.toMap(Role::getName, r -> r));

    // Remover UserRole que ya no deben estar
    user.getRoles()
        .removeIf(
            userRole -> {
              RoleName roleName = userRole.getRole().getName();
              if (!roles.contains(roleName)) {
                log.debug("Removing role {} from user {}", roleName, userId);
                return true;
              }
              return false;
            });

    // Agregar solo los roles nuevos (que no existen actualmente)
    Set<RoleName> existingRoleNames =
        user.getRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

    for (RoleName roleName : roles) {
      if (!existingRoleNames.contains(roleName)) {
        Role role = rolesByName.get(roleName);
        if (role != null) {
          UserRole userRole = new UserRole(user, role);
          user.getRoles().add(userRole);
          log.debug("Adding role {} to user {}", roleName, userId);
        }
      }
    }

    // 8. Persistir cambios
    user = userRepository.save(user);

    log.info(
        "RoleChanged userId={} oldRoles={} newRoles={} by={}",
        userId,
        currentRoles,
        roles,
        performedBy);

    // Registrar auditoría persistente - US07
    String auditDetails =
        String.format(
            "{\"oldRoles\":%s,\"newRoles\":%s}",
            toJsonArray(currentRoles), toJsonArray(roles));
    logAuditAction(userId, "ROLE_CHANGED", performedBy, auditDetails);

    return mapToUserResponseDTO(user);
  }

  @Override
  @Transactional
  public UserResponseDTO updateStatus(Long userId, boolean isActive, String performedBy) {
    log.info(
        "AdminUserService.updateStatus: userId={} isActive={} performedBy={}",
        userId,
        isActive,
        performedBy);

    // 1. Buscar usuario
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("User not found with id: {}", userId);
                  return new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado.");
                });

    boolean oldStatus = user.isActive();
    log.debug("Current status for user {}: {}", userId, oldStatus);

    // Si no hay cambio, retornar sin hacer nada
    if (oldStatus == isActive) {
      log.debug("No status change for user {}", userId);
      return mapToUserResponseDTO(user);
    }

    // 2. Sincronizar con Cognito primero (fuente de verdad para acceso)
    String username = user.getEmail();

    try {
      if (isActive) {
        cognitoAdminClient.enableUser(username);
        log.info("User {} enabled in Cognito", username);
      } else {
        cognitoAdminClient.disableUser(username);
        log.info("User {} disabled in Cognito", username);
      }

    } catch (Exception e) {
      log.error("Error updating user status in Cognito for user {}: {}", userId, e.getMessage(), e);
      throw new RuntimeException(
          "Error al actualizar estado en Cognito. El cambio no se aplicó en DB.", e);
    }

    // 3. Actualizar en DB (espejo)
    user.setActive(isActive);
    user = userRepository.save(user);

    log.info(
        "StatusChanged userId={} oldStatus={} newStatus={} by={}",
        userId,
        oldStatus,
        isActive,
        performedBy);

    // Registrar auditoría persistente - US07
    String auditDetails =
        String.format("{\"oldStatus\":%s,\"newStatus\":%s}", oldStatus, isActive);
    logAuditAction(userId, "STATUS_CHANGED", performedBy, auditDetails);

    return mapToUserResponseDTO(user);
  }

  @Override
  @Transactional
  public UserResponseDTO syncUser(Long userId) {
    log.info("AdminUserService.syncUser: syncing user {}", userId);

    // 1. Buscar usuario en DB
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("User not found with id: {}", userId);
                  return new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado.");
                });

    String email = user.getEmail();
    log.debug("Syncing user {} ({})", userId, email);

    // 2. Obtener usuario de Cognito (lanza NotFoundException si no existe)
    CognitoUserInfo cognitoUser = cognitoAdminClient.getUser(email);
    log.debug("User found in Cognito with groups: {}", cognitoUser.getGroups());

    // 3. Mapear grupos de Cognito a roles válidos
    Set<RoleName> cognitoRoles = mapCognitoGroupsToRoles(cognitoUser.getGroups());
    log.debug("Cognito roles mapped: {}", cognitoRoles);

    // 4. Obtener roles actuales en DB
    Set<RoleName> currentRoles =
        user.getRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());
    log.debug("Current DB roles: {}", currentRoles);

    // 5. Sincronizar roles si hay diferencias
    if (!cognitoRoles.equals(currentRoles)) {
      log.info("Role mismatch detected. Syncing from Cognito to DB...");

      // Calcular cambios
      Set<RoleName> rolesToAdd = new java.util.HashSet<>(cognitoRoles);
      rolesToAdd.removeAll(currentRoles);

      Set<RoleName> rolesToRemove = new java.util.HashSet<>(currentRoles);
      rolesToRemove.removeAll(cognitoRoles);

      log.debug("Roles to add: {}", rolesToAdd);
      log.debug("Roles to remove: {}", rolesToRemove);

      // Obtener entidades de roles
      List<Role> roleEntities = roleRepository.findByNameIn(cognitoRoles);
      Map<RoleName, Role> rolesByName =
          roleEntities.stream().collect(Collectors.toMap(Role::getName, r -> r));

      // Remover roles que ya no existen en Cognito
      user.getRoles()
          .removeIf(
              userRole -> {
                RoleName roleName = userRole.getRole().getName();
                if (!cognitoRoles.contains(roleName)) {
                  log.debug("Removing role {} from user {}", roleName, userId);
                  return true;
                }
                return false;
              });

      // Agregar roles nuevos desde Cognito
      Set<RoleName> existingRoleNames =
          user.getRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

      for (RoleName roleName : cognitoRoles) {
        if (!existingRoleNames.contains(roleName)) {
          Role role = rolesByName.get(roleName);
          if (role != null) {
            UserRole userRole = new UserRole(user, role);
            user.getRoles().add(userRole);
            log.debug("Adding role {} to user {}", roleName, userId);
          }
        }
      }

      // Persistir cambios
      user = userRepository.save(user);

      // Registrar auditoría
      String details =
          String.format(
              "{\"source\":\"cognito\",\"oldRoles\":%s,\"newRoles\":%s}",
              toJsonArray(currentRoles), toJsonArray(cognitoRoles));
      logAuditAction(userId, "USER_SYNCED", "SYSTEM", details);

      log.info(
          "User {} synced successfully. Roles updated: {} -> {}",
          userId,
          currentRoles,
          cognitoRoles);
    } else {
      log.info("User {} already in sync with Cognito. No changes needed.", userId);

      // Registrar auditoría de sync sin cambios
      logAuditAction(
          userId, "USER_SYNCED", "SYSTEM", "{\"source\":\"cognito\",\"changes\":\"none\"}");
    }

    return mapToUserResponseDTO(user);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<UserAuditLogDTO> getUserAuditLog(Long userId, Pageable pageable) {
    log.info(
        "AdminUserService.getUserAuditLog: userId={} page={} size={}",
        userId,
        pageable.getPageNumber(),
        pageable.getPageSize());

    // 1. Validar que usuario existe
    if (!userRepository.existsById(userId)) {
      log.warn("User not found with id: {}", userId);
      throw new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado.");
    }

    // 2. Consultar logs de auditoría con paginación
    Page<UserAuditLog> page =
        userAuditLogRepository.findByUserIdOrderByChangedAtDesc(userId, pageable);

    log.debug("Found {} audit logs for user {}", page.getTotalElements(), userId);

    // 3. Mapear a DTOs
    List<UserAuditLogDTO> content = page.getContent().stream().map(this::mapToAuditLogDTO).toList();

    return new PageResponse<>(
        content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  /**
   * Registra una acción de auditoría en DB y log.
   *
   * @param userId ID del usuario afectado
   * @param action acción realizada (ej: ROLE_CHANGED, STATUS_CHANGED)
   * @param changedBy email del administrador o SYSTEM (puede ser null)
   * @param details detalles adicionales en formato JSON
   */
  private void logAuditAction(Long userId, String action, String changedBy, String details) {
    try {
      // Usar "UNKNOWN" si changedBy es null o vacío para evitar violación de NOT NULL constraint
      String effectiveChangedBy =
          (changedBy != null && !changedBy.isBlank()) ? changedBy : "UNKNOWN";

      // Log warning si el changedBy era null (indica problema en la extracción del email del JWT)
      if (changedBy == null || changedBy.isBlank()) {
        log.warn(
            "changedBy is null/blank for audit action userId={} action={}. Using 'UNKNOWN'. "
                + "Check JWT email claim extraction.",
            userId,
            action);
      }

      UserAuditLog auditLog =
          UserAuditLog.builder()
              .userId(userId)
              .action(action)
              .changedBy(effectiveChangedBy)
              .details(details)
              .build();

      userAuditLogRepository.save(auditLog);
      log.info("Audit log created: userId={} action={} by={}", userId, action, effectiveChangedBy);

    } catch (Exception e) {
      // No fallar la operación principal si falla la auditoría
      log.error(
          "Failed to save audit log for userId={} action={}: {}",
          userId,
          action,
          e.getMessage(),
          e);
    }
  }

  /**
   * Mapea UserAuditLog a DTO.
   */
  private UserAuditLogDTO mapToAuditLogDTO(UserAuditLog log) {
    return new UserAuditLogDTO(
        log.getId(),
        log.getUserId(),
        log.getAction(),
        log.getChangedBy(),
        log.getChangedAt(),
        log.getDetails());
  }

  /**
   * Convierte un Set de RoleName a array JSON string.
   */
  private String toJsonArray(Set<RoleName> roles) {
    if (roles == null || roles.isEmpty()) {
      return "[]";
    }
    return "["
        + roles.stream().map(r -> "\"" + r.name() + "\"").collect(Collectors.joining(","))
        + "]";
  }

  /**
   * Normaliza email: trim y lowercase.
   *
   * @param email email original
   * @return email normalizado
   */
  private String normalizeEmail(String email) {
    if (email == null) return null;
    return email.trim().toLowerCase();
  }

  /**
   * Asigna roles al usuario en DB.
   *
   * @param user usuario
   * @param roleNames nombres de roles a asignar
   */
  private void assignRolesToUser(AppUser user, Set<RoleName> roleNames) {
    if (roleNames == null || roleNames.isEmpty()) return;

    List<Role> roles = roleRepository.findByNameIn(roleNames);
    if (roles.isEmpty()) {
      log.warn("No roles found in DB for names: {}", roleNames);
      return;
    }

    for (Role role : roles) {
      UserRole userRole = new UserRole(user, role);
      user.getRoles().add(userRole);
      log.debug("Role {} assigned to user {}", role.getName(), user.getEmail());
    }
  }

  /**
   * Mapea AppUser a UserResponseDTO.
   *
   * @param user entidad AppUser
   * @return DTO para response
   */
  private UserResponseDTO mapToUserResponseDTO(AppUser user) {
    Set<String> roleNames =
        user.getRoles().stream()
            .map(ur -> ur.getRole().getName().name())
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return UserResponseDTO.builder()
        .id(user.getId())
        .cognitoUserId(user.getCognitoUserId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .roles(roleNames)
        .build();
  }

  /**
   * Obtiene los IDs de usuarios que tienen pedidos pendientes (status != DELIVERED/CANCELLED).
   *
   * @param userIds lista de IDs de usuarios a verificar
   * @return conjunto de IDs con pedidos pendientes
   */
  private Set<Long> getUsersWithPendingOrders(List<Long> userIds) {
    if (userIds.isEmpty()) return Set.of();

    // Query optimizada: SELECT DISTINCT user_id FROM customer_order
    // WHERE user_id IN (:userIds) AND status NOT IN ('DELIVERED', 'CANCELLED')
    return customerOrderRepository
        .findAll(
            (root, query, cb) -> {
              query.distinct(true);
              return cb.and(
                  root.get("user").get("id").in(userIds),
                  cb.not(
                      root.get("status").in(OrderStatus.DELIVERED, OrderStatus.CANCELLED)));
            })
        .stream()
        .map(order -> order.getUser().getId())
        .collect(Collectors.toSet());
  }

  /**
   * Mapea entidad AppUser a DTO con roles y flag de pedidos pendientes.
   *
   * @param user entidad AppUser (con roles cargados)
   * @param usersWithPendingOrders set de IDs con pedidos pendientes
   * @return DTO para Admin
   */
  private AdminUserListItemDTO mapToDTO(AppUser user, Set<Long> usersWithPendingOrders) {
    Set<String> roleNames =
        user.getRoles().stream()
            .map(userRole -> userRole.getRole().getName().name())
            .collect(Collectors.toSet());

    boolean hasPending = usersWithPendingOrders.contains(user.getId());

    return new AdminUserListItemDTO(
        user.getId(),
        user.getCognitoUserId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone(),
        user.isActive(),
        roleNames,
        hasPending);
  }

  /**
   * Capitaliza la primera letra de cada palabra en un string.
   *
   * @param text texto a capitalizar
   * @return texto con primera letra de cada palabra en mayúscula
   */
  private String capitalize(String text) {
    if (text == null || text.isBlank()) return text;

    String[] words = text.split("\\s+");
    StringBuilder result = new StringBuilder();

    for (String word : words) {
      if (!word.isEmpty()) {
        if (result.length() > 0) {
          result.append(" ");
        }
        result.append(Character.toUpperCase(word.charAt(0)));
        if (word.length() > 1) {
          result.append(word.substring(1).toLowerCase());
        }
      }
    }

    return result.toString();
  }
}
