package com.cocinadelicia.backend.user.service.impl;

import com.cocinadelicia.backend.auth.cognito.CognitoUserInfoClient;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.user.dto.UserRegistrationDTO;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.repository.UserRoleRepository;
import com.cocinadelicia.backend.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final UserRoleRepository userRoleRepo;
  private final CognitoUserInfoClient userInfoClient;

  @Override
  @Transactional
  public UserResponseDTO registerOrUpdateFromToken(
      UserRegistrationDTO body, JwtAuthenticationToken auth) {

    var jwt = auth.getToken();

    final String sub = jwt.getClaimAsString("sub");
    @SuppressWarnings("unchecked")
    final List<String> groups =
        Optional.ofNullable((List<String>) jwt.getClaim("cognito:groups")).orElseGet(List::of);

    final String accessTokenValue = jwt.getTokenValue();
    Map<String, Object> userInfo = userInfoClient.fetchUserInfo(accessTokenValue);

    String rawEmail = asString(userInfo.get("email"));
    String givenName =
        firstNonBlank(
            asString(userInfo.get("given_name")), body != null ? body.getFirstName() : null);
    String familyName =
        firstNonBlank(
            asString(userInfo.get("family_name")), body != null ? body.getLastName() : null);
    String phone =
        firstNonBlank(
            asString(userInfo.get("phone_number")), body != null ? body.getPhone() : null);

    if (rawEmail == null || rawEmail.isBlank()) {
      throw new UserService.MissingEmailException(
          "Cognito userInfo did not return email. Ensure scopes 'email profile' and attribute mapping are configured for the App Client/IdP.");
    }

    final String email = rawEmail.trim().toLowerCase(Locale.ROOT);

    Optional<AppUser> bySub = userRepo.findByCognitoUserId(sub);
    Optional<AppUser> byEmail = userRepo.findByEmail(email);

    if (bySub.isPresent()) {
      AppUser u = bySub.get();
      if (byEmail.isPresent() && !Objects.equals(byEmail.get().getId(), u.getId())) {
        throw new UserService.EmailConflictException(email);
      }
      if (givenName != null && !givenName.isBlank()) u.setFirstName(givenName);
      if (familyName != null && !familyName.isBlank()) u.setLastName(familyName);
      if (phone != null && !phone.isBlank()) u.setPhone(phone);
      u.setEmail(email);

      addMissingRoles(u, groups);
      AppUser saved = userRepo.save(u);
      return toDto(saved);
    }

    if (byEmail.isPresent()) {
      throw new UserService.EmailConflictException(email);
    }

    AppUser created =
        AppUser.builder()
            .cognitoUserId(sub)
            .email(email)
            .firstName(Objects.requireNonNullElse(givenName, ""))
            .lastName(Objects.requireNonNullElse(familyName, ""))
            .phone(phone)
            .isActive(true)
            .build();

    created = userRepo.save(created);
    addMissingRoles(created, groups);
    created = userRepo.save(created);

    return toDto(created);
  }

  @Override
  public Long resolveUserIdFromJwt(Jwt jwt) {
    final String sub = jwt.getClaim("sub");
    return userRepo
        .findByCognitoUserId(sub)
        .map(AppUser::getId)
        .orElseThrow(
            () -> new NotFoundException("Usuario no registrado en app_user para sub=" + sub));
  }

  /* ===== Helpers ===== */

  private void addMissingRoles(AppUser user, List<String> groups) {
    if (groups == null || groups.isEmpty()) return;

    Set<RoleName> requested =
        groups.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .map(String::toUpperCase)
            .map(
                s -> {
                  try {
                    return RoleName.valueOf(s);
                  } catch (IllegalArgumentException ex) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

    if (requested.isEmpty()) return;

    List<Role> existing = roleRepo.findByNameIn(requested);
    if (existing.isEmpty()) return;

    Map<RoleName, Role> roleByName =
        existing.stream().collect(Collectors.toMap(Role::getName, r -> r));

    for (RoleName rn : requested) {
      Role r = roleByName.get(rn);
      if (r == null) continue;
      boolean hasIt =
          user.getRoles().stream().anyMatch(ur -> ur.getRole().getId().equals(r.getId()));
      if (!hasIt) {
        UserRole ur = new UserRole(user, r);
        user.getRoles().add(ur);
        // si tenés cascada en AppUser.roles, alcanza con agregar al set.
        // Si no, persistí explícitamente: userRoleRepo.save(ur);
      }
    }
  }

  private UserResponseDTO toDto(AppUser u) {
    Set<String> roleNames =
        u.getRoles().stream()
            .map(ur -> ur.getRole().getName().name())
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return UserResponseDTO.builder()
        .id(u.getId())
        .cognitoUserId(u.getCognitoUserId())
        .email(u.getEmail())
        .firstName(u.getFirstName())
        .lastName(u.getLastName())
        .phone(u.getPhone())
        .roles(roleNames)
        .build();
  }

  private static String firstNonBlank(String a, String b) {
    if (a != null && !a.isBlank()) return a;
    if (b != null && !b.isBlank()) return b;
    return null;
  }

  private static String asString(Object o) {
    return o == null ? null : o.toString();
  }
}
