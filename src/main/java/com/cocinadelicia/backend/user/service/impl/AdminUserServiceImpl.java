package com.cocinadelicia.backend.user.service.impl;

import static com.cocinadelicia.backend.user.repository.spec.UserSpecifications.*;

import com.cocinadelicia.backend.auth.cognito.CognitoAdminClient;
import com.cocinadelicia.backend.auth.cognito.CognitoUserInfo;
import com.cocinadelicia.backend.common.exception.ConflictException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.service.AdminUserService;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
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

    // 7. TODO US07: Registrar auditoría persistente (user_audit_log)
    // auditLogRepository.save(new UserAuditLog(newUser.getId(), "USER_INVITED", adminUsername,
    // roles...))
    log.debug("TODO US07: Register audit log for USER_INVITED action");

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
    AppUser newUser =
        AppUser.builder()
            .cognitoUserId(cognitoUser.getCognitoUserId())
            .email(normalizedEmail)
            .firstName(cognitoUser.getFirstName())
            .lastName(cognitoUser.getLastName())
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

    // 7. TODO US07: Registrar auditoría persistente (user_audit_log)
    log.debug("TODO US07: Register audit log for USER_IMPORTED action");

    // 8. Mapear a DTO y retornar
    return mapToUserResponseDTO(newUser);
  }

  /**
   * Mapea grupos de Cognito a roles válidos del sistema.
   *
   * <p>Filtra solo grupos que coincidan con RoleName (ADMIN, CHEF, COURIER, CUSTOMER). Loguea
   * warning para grupos desconocidos.
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
      if (validRoleNames.contains(group)) {
        validRoles.add(RoleName.valueOf(group));
      } else {
        unknownGroups.add(group);
      }
    }

    if (!unknownGroups.isEmpty()) {
      log.warn("Ignoring unknown Cognito groups (not mapped to RoleName): {}", unknownGroups);
    }

    log.debug(
        "Mapped {} Cognito groups to {} valid roles", cognitoGroups.size(), validRoles.size());
    return validRoles;
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
}
