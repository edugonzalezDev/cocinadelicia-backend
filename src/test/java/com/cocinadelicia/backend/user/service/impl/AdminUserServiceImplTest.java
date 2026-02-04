package com.cocinadelicia.backend.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.cocinadelicia.backend.auth.cognito.CognitoAdminClient;
import com.cocinadelicia.backend.auth.cognito.CognitoUserInfo;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private CustomerOrderRepository customerOrderRepository;
  @Mock private CognitoAdminClient cognitoAdminClient;
  @Mock private RoleRepository roleRepository;

  @InjectMocks private AdminUserServiceImpl adminUserService;

  private Role courierRole;
  private Role adminRole;
  private Role chefRole;
  private Role customerRole;

  @BeforeEach
  void setUp() {
    courierRole = new Role();
    courierRole.setId(3L);
    courierRole.setName(RoleName.COURIER);

    adminRole = new Role();
    adminRole.setId(1L);
    adminRole.setName(RoleName.ADMIN);

    chefRole = new Role();
    chefRole.setId(2L);
    chefRole.setName(RoleName.CHEF);

    customerRole = new Role();
    customerRole.setId(4L);
    customerRole.setName(RoleName.CUSTOMER);
  }

  @Test
  void importUser_withLowercaseGroupsFromCognito_shouldMapCorrectly() {
    // Arrange
    String email = "courier@example.com";
    ImportUserRequest request = new ImportUserRequest(email);

    // Cognito devuelve grupos en minúsculas (caso real del log)
    CognitoUserInfo cognitoUser =
        CognitoUserInfo.builder()
            .cognitoUserId("cognito-sub-123")
            .email(email)
            .firstName("Test")
            .lastName("Courier")
            .phone("+59899123456")
            .groups(Set.of("courier")) // MINÚSCULAS desde Cognito
            .build();

    when(cognitoAdminClient.getUser(email)).thenReturn(cognitoUser);
    when(userRepository.findByCognitoUserId("cognito-sub-123")).thenReturn(Optional.empty());
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Mock para roles
    when(roleRepository.findByNameIn(Set.of(RoleName.COURIER))).thenReturn(List.of(courierRole));

    // Mock save - capturar el usuario guardado
    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    when(userRepository.save(any(AppUser.class)))
        .thenAnswer(
            invocation -> {
              AppUser user = invocation.getArgument(0);
              if (user.getId() == null) {
                user.setId(100L);
              }
              return user;
            });

    // Act
    UserResponseDTO result = adminUserService.importUser(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getRoles()).containsExactly("COURIER"); // Debe mapearse a COURIER en mayúsculas

    // Verificar que se guardó el usuario con el rol correcto
    verify(userRepository, atLeastOnce()).save(userCaptor.capture());
    AppUser savedUser = userCaptor.getValue();
    assertThat(savedUser.getRoles()).hasSize(1);
    assertThat(savedUser.getRoles().iterator().next().getRole().getName())
        .isEqualTo(RoleName.COURIER);
  }

  @Test
  void importUser_withUppercaseGroupsFromCognito_shouldStillWork() {
    // Arrange
    String email = "admin@example.com";
    ImportUserRequest request = new ImportUserRequest(email);

    // Cognito devuelve grupos en MAYÚSCULAS
    CognitoUserInfo cognitoUser =
        CognitoUserInfo.builder()
            .cognitoUserId("cognito-sub-456")
            .email(email)
            .firstName("Test")
            .lastName("Admin")
            .phone("+59899654321")
            .groups(Set.of("ADMIN")) // MAYÚSCULAS
            .build();

    when(cognitoAdminClient.getUser(email)).thenReturn(cognitoUser);
    when(userRepository.findByCognitoUserId("cognito-sub-456")).thenReturn(Optional.empty());
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByNameIn(Set.of(RoleName.ADMIN))).thenReturn(List.of(adminRole));

    when(userRepository.save(any(AppUser.class)))
        .thenAnswer(
            invocation -> {
              AppUser user = invocation.getArgument(0);
              if (user.getId() == null) {
                user.setId(200L);
              }
              return user;
            });

    // Act
    UserResponseDTO result = adminUserService.importUser(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRoles()).containsExactly("ADMIN");
  }

  @Test
  void importUser_withMixedCaseGroupsFromCognito_shouldNormalizeAll() {
    // Arrange
    String email = "multiuser@example.com";
    ImportUserRequest request = new ImportUserRequest(email);

    // Cognito devuelve grupos en casos mixtos
    CognitoUserInfo cognitoUser =
        CognitoUserInfo.builder()
            .cognitoUserId("cognito-sub-789")
            .email(email)
            .firstName("Multi")
            .lastName("Role")
            .phone("+59899111222")
            .groups(Set.of("courier", "Chef", "CUSTOMER")) // Casos mixtos
            .build();

    when(cognitoAdminClient.getUser(email)).thenReturn(cognitoUser);
    when(userRepository.findByCognitoUserId("cognito-sub-789")).thenReturn(Optional.empty());
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByNameIn(Set.of(RoleName.COURIER, RoleName.CHEF, RoleName.CUSTOMER)))
        .thenReturn(List.of(courierRole, chefRole, customerRole));

    when(userRepository.save(any(AppUser.class)))
        .thenAnswer(
            invocation -> {
              AppUser user = invocation.getArgument(0);
              if (user.getId() == null) {
                user.setId(300L);
              }
              return user;
            });

    // Act
    UserResponseDTO result = adminUserService.importUser(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRoles()).containsExactlyInAnyOrder("COURIER", "CHEF", "CUSTOMER");
  }

  @Test
  void importUser_withInvalidGroupsFromCognito_shouldIgnoreInvalid() {
    // Arrange
    String email = "partial@example.com";
    ImportUserRequest request = new ImportUserRequest(email);

    // Cognito devuelve grupos válidos e inválidos
    CognitoUserInfo cognitoUser =
        CognitoUserInfo.builder()
            .cognitoUserId("cognito-sub-999")
            .email(email)
            .firstName("Partial")
            .lastName("User")
            .phone("+59899333444")
            .groups(Set.of("courier", "invalid_group", "unknown")) // Mixto: válido + inválidos
            .build();

    when(cognitoAdminClient.getUser(email)).thenReturn(cognitoUser);
    when(userRepository.findByCognitoUserId("cognito-sub-999")).thenReturn(Optional.empty());
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByNameIn(Set.of(RoleName.COURIER))).thenReturn(List.of(courierRole));

    when(userRepository.save(any(AppUser.class)))
        .thenAnswer(
            invocation -> {
              AppUser user = invocation.getArgument(0);
              if (user.getId() == null) {
                user.setId(400L);
              }
              return user;
            });

    // Act
    UserResponseDTO result = adminUserService.importUser(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRoles()).containsExactly("COURIER"); // Solo el válido
  }
}
