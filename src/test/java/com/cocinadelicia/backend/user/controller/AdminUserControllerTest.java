package com.cocinadelicia.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.config.SecurityConfig;
import com.cocinadelicia.backend.common.exception.ConflictException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.ImportUserRequest;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
import com.cocinadelicia.backend.user.dto.UpdateUserProfileRequest;
import com.cocinadelicia.backend.user.dto.UserResponseDTO;
import com.cocinadelicia.backend.user.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminUserController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {"cdd.security.groups-claim=cognito:groups"})
@ActiveProfiles("test")
class AdminUserControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AdminUserService adminUserService;
  @MockitoBean private JwtDecoder jwtDecoder;

  private PageResponse<AdminUserListItemDTO> mockResponse;

  @BeforeEach
  void setup() {
    reset(adminUserService);

    // Mock response estándar
    AdminUserListItemDTO user1 =
        new AdminUserListItemDTO(
            1L,
            "cognito-123",
            "juan@example.com",
            "Juan",
            "Pérez",
            "+59899111111",
            true,
            Set.of("ADMIN"),
            false);

    mockResponse = new PageResponse<>(List.of(user1), 0, 20, 1, 1);

    when(adminUserService.listUsers(any(), any())).thenReturn(mockResponse);
  }

  @Test
  void listUsers_withoutAuth_returns401() throws Exception {
    mvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());

    verify(adminUserService, never()).listUsers(any(), any());
  }

  @Test
  void listUsers_withCustomerRole_returns403() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).listUsers(any(), any());
  }

  @Test
  void listUsers_withChefRole_returns403() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CHEF"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).listUsers(any(), any());
  }

  @Test
  void listUsers_withAdminRole_returns200() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].email").value("juan@example.com"))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20));

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withSearchParam_passesFilterToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("q", "juan")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withRolesFilter_passesToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("roles", "ADMIN", "CHEF")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withIsActiveFilter_passesToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("isActive", "true")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withHasPendingOrdersFilter_passesToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("hasPendingOrders", "true")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withPagination_passesToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "email,asc")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  @Test
  void listUsers_withAllFilters_passesToService() throws Exception {
    mvc.perform(
            get("/api/admin/users")
                .param("q", "juan")
                .param("roles", "ADMIN")
                .param("isActive", "true")
                .param("hasPendingOrders", "false")
                .param("page", "0")
                .param("size", "20")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());

    verify(adminUserService, times(1)).listUsers(any(), any());
  }

  // ========== Tests para POST /api/admin/users/invite ==========

  @Test
  void inviteUser_withoutAuth_returns401() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "nuevo@example.com", "Nuevo", "Usuario", "+59899222222", Set.of(RoleName.CUSTOMER));

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(adminUserService, never()).inviteUser(any());
  }

  @Test
  void inviteUser_withCustomerRole_returns403() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "nuevo@example.com", "Nuevo", "Usuario", "+59899222222", Set.of(RoleName.CUSTOMER));

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).inviteUser(any());
  }

  @Test
  void inviteUser_withAdminRole_validRequest_returns201() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "nuevo@example.com", "Nuevo", "Usuario", "+59899222222", Set.of(RoleName.CUSTOMER));

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(10L)
            .cognitoUserId("cognito-abc-123")
            .email("nuevo@example.com")
            .firstName("Nuevo")
            .lastName("Usuario")
            .phone("+59899222222")
            .roles(Set.of("CUSTOMER"))
            .build();

    when(adminUserService.inviteUser(any())).thenReturn(mockUser);

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.email").value("nuevo@example.com"))
        .andExpect(jsonPath("$.cognitoUserId").value("cognito-abc-123"))
        .andExpect(jsonPath("$.roles").isArray())
        .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));

    verify(adminUserService, times(1)).inviteUser(any());
  }

  @Test
  void inviteUser_withoutEmail_returns400() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(null, "Nuevo", "Usuario", "+59899222222", Set.of(RoleName.CUSTOMER));

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.email").exists());

    verify(adminUserService, never()).inviteUser(any());
  }

  @Test
  void inviteUser_withInvalidEmail_returns400() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "invalid-email", "Nuevo", "Usuario", "+59899222222", Set.of(RoleName.CUSTOMER));

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.email").exists());

    verify(adminUserService, never()).inviteUser(any());
  }

  @Test
  void inviteUser_withEmptyRoles_returns400() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest("nuevo@example.com", "Nuevo", "Usuario", "+59899222222", Set.of());

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.roles").exists());

    verify(adminUserService, never()).inviteUser(any());
  }

  @Test
  void inviteUser_withDuplicateEmail_returns409() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "duplicado@example.com",
            "Duplicado",
            "Usuario",
            "+59899222222",
            Set.of(RoleName.CUSTOMER));

    when(adminUserService.inviteUser(any()))
        .thenThrow(new ConflictException("EMAIL_CONFLICT", "El email ya está registrado."));

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("EMAIL_CONFLICT"))
        .andExpect(jsonPath("$.message").value("El email ya está registrado."));

    verify(adminUserService, times(1)).inviteUser(any());
  }

  @Test
  void inviteUser_withMultipleRoles_returns201() throws Exception {
    InviteUserRequest request =
        new InviteUserRequest(
            "multirole@example.com",
            "Multi",
            "Role",
            "+59899222222",
            Set.of(RoleName.CHEF, RoleName.CUSTOMER));

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(11L)
            .cognitoUserId("cognito-multi-123")
            .email("multirole@example.com")
            .firstName("Multi")
            .lastName("Role")
            .phone("+59899222222")
            .roles(Set.of("CHEF", "CUSTOMER"))
            .build();

    when(adminUserService.inviteUser(any())).thenReturn(mockUser);

    mvc.perform(
            post("/api/admin/users/invite")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("multirole@example.com"))
        .andExpect(jsonPath("$.roles").isArray());

    verify(adminUserService, times(1)).inviteUser(any());
  }

  // ========== Tests para POST /api/admin/users/import ==========

  @Test
  void importUser_withoutAuth_returns401() throws Exception {
    ImportUserRequest request = new ImportUserRequest("existente@example.com");

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(adminUserService, never()).importUser(any());
  }

  @Test
  void importUser_withCustomerRole_returns403() throws Exception {
    ImportUserRequest request = new ImportUserRequest("existente@example.com");

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).importUser(any());
  }

  @Test
  void importUser_withAdminRole_validRequest_returns201() throws Exception {
    ImportUserRequest request = new ImportUserRequest("existente@example.com");

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(20L)
            .cognitoUserId("cognito-existing-123")
            .email("existente@example.com")
            .firstName("Usuario")
            .lastName("Existente")
            .phone("+59899333333")
            .roles(Set.of("CUSTOMER", "CHEF"))
            .build();

    when(adminUserService.importUser(any())).thenReturn(mockUser);

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(20))
        .andExpect(jsonPath("$.email").value("existente@example.com"))
        .andExpect(jsonPath("$.cognitoUserId").value("cognito-existing-123"))
        .andExpect(jsonPath("$.roles").isArray());

    verify(adminUserService, times(1)).importUser(any());
  }

  @Test
  void importUser_withoutEmail_returns400() throws Exception {
    ImportUserRequest request = new ImportUserRequest(null);

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.email").exists());

    verify(adminUserService, never()).importUser(any());
  }

  @Test
  void importUser_withInvalidEmail_returns400() throws Exception {
    ImportUserRequest request = new ImportUserRequest("not-an-email");

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.email").exists());

    verify(adminUserService, never()).importUser(any());
  }

  @Test
  void importUser_userNotFoundInCognito_returns404() throws Exception {
    ImportUserRequest request = new ImportUserRequest("noexiste@example.com");

    when(adminUserService.importUser(any()))
        .thenThrow(
            new NotFoundException(
                "USER_NOT_FOUND_IN_COGNITO", "Usuario no existe en Cognito."));

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND_IN_COGNITO"));

    verify(adminUserService, times(1)).importUser(any());
  }

  @Test
  void importUser_userAlreadyImported_returns409() throws Exception {
    ImportUserRequest request = new ImportUserRequest("yaImportado@example.com");

    when(adminUserService.importUser(any()))
        .thenThrow(
            new ConflictException("USER_ALREADY_IMPORTED", "El usuario ya está importado."));

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_ALREADY_IMPORTED"));

    verify(adminUserService, times(1)).importUser(any());
  }

  @Test
  void importUser_emailConflict_returns409() throws Exception {
    ImportUserRequest request = new ImportUserRequest("conflicto@example.com");

    when(adminUserService.importUser(any()))
        .thenThrow(
            new ConflictException("EMAIL_CONFLICT", "Ya existe usuario con ese email."));

    mvc.perform(
            post("/api/admin/users/import")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("EMAIL_CONFLICT"));

    verify(adminUserService, times(1)).importUser(any());
  }

  // ==================== Tests PATCH /api/admin/users/{id} ====================

  @Test
  void updateUserProfile_withAdminRole_validRequest_returns200() throws Exception {
    UpdateUserProfileRequest request = new UpdateUserProfileRequest("Juan", "Pérez", "+59899555555");

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(1L)
            .cognitoUserId("cognito-123")
            .email("juan@example.com")
            .firstName("Juan")
            .lastName("Pérez")
            .phone("+59899555555")
            .roles(Set.of("CUSTOMER"))
            .build();

    when(adminUserService.updateUserProfile(eq(1L), any())).thenReturn(mockUser);

    mvc.perform(
            patch("/api/admin/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("juan@example.com"))
        .andExpect(jsonPath("$.firstName").value("Juan"))
        .andExpect(jsonPath("$.lastName").value("Pérez"))
        .andExpect(jsonPath("$.phone").value("+59899555555"));

    verify(adminUserService, times(1)).updateUserProfile(eq(1L), any());
  }

  @Test
  void updateUserProfile_withoutAuth_returns401() throws Exception {
    UpdateUserProfileRequest request = new UpdateUserProfileRequest("Juan", null, null);

    mvc.perform(
            patch("/api/admin/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(adminUserService, never()).updateUserProfile(any(), any());
  }

  @Test
  void updateUserProfile_withCustomerRole_returns403() throws Exception {
    UpdateUserProfileRequest request = new UpdateUserProfileRequest("Juan", null, null);

    mvc.perform(
            patch("/api/admin/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).updateUserProfile(any(), any());
  }

  @Test
  void updateUserProfile_userNotFound_returns404() throws Exception {
    UpdateUserProfileRequest request = new UpdateUserProfileRequest("Juan", "Pérez", null);

    when(adminUserService.updateUserProfile(eq(999L), any()))
        .thenThrow(new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

    mvc.perform(
            patch("/api/admin/users/999")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

    verify(adminUserService, times(1)).updateUserProfile(eq(999L), any());
  }

  @Test
  void updateUserProfile_fieldTooLong_returns400() throws Exception {
    // Campo firstName excede el límite de 191 caracteres
    String longName = "a".repeat(192);
    UpdateUserProfileRequest request = new UpdateUserProfileRequest(longName, null, null);

    mvc.perform(
            patch("/api/admin/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields").exists())
        .andExpect(jsonPath("$.fields.firstName").exists());

    verify(adminUserService, never()).updateUserProfile(any(), any());
  }

  @Test
  void updateUserProfile_partialUpdate_returns200() throws Exception {
    // Solo actualizar teléfono
    UpdateUserProfileRequest request = new UpdateUserProfileRequest(null, null, "+59899999999");

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(1L)
            .cognitoUserId("cognito-123")
            .email("juan@example.com")
            .firstName("Juan")
            .lastName("Original")
            .phone("+59899999999")
            .roles(Set.of("CUSTOMER"))
            .build();

    when(adminUserService.updateUserProfile(eq(1L), any())).thenReturn(mockUser);

    mvc.perform(
            patch("/api/admin/users/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phone").value("+59899999999"));

    verify(adminUserService, times(1)).updateUserProfile(eq(1L), any());
  }

  // ==================== US05: PUT /api/admin/users/{id}/roles ====================

  @Test
  void updateRoles_withoutAuth_returns401() throws Exception {
    String requestBody = "{\"roles\": [\"CUSTOMER\", \"CHEF\"]}";

    mvc.perform(
            post("/api/admin/users/1/roles")
                .contentType("application/json")
                .content(requestBody))
        .andExpect(status().isUnauthorized());

    verify(adminUserService, never()).updateRoles(any(), any(), any(), any());
  }

  @Test
  void updateRoles_withCustomerRole_returns403() throws Exception {
    String requestBody = "{\"roles\": [\"CUSTOMER\", \"CHEF\"]}";

    mvc.perform(
            post("/api/admin/users/1/roles")
                .contentType("application/json")
                .content(requestBody)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).updateRoles(any(), any(), any(), any());
  }

  @Test
  void updateRoles_happyPath_returns200() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateRolesRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateRolesRequest(
            Set.of(RoleName.CUSTOMER, RoleName.CHEF), null);

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(1L)
            .cognitoUserId("cognito-123")
            .email("user@example.com")
            .firstName("Test")
            .lastName("User")
            .phone("+59899111111")
            .roles(Set.of("CUSTOMER", "CHEF"))
            .build();

    when(adminUserService.updateRoles(eq(1L), any(), any(), any())).thenReturn(mockUser);

    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/admin/users/1/roles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.roles").isArray())
        .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasSize(2)));

    verify(adminUserService, times(1))
        .updateRoles(eq(1L), eq(Set.of(RoleName.CUSTOMER, RoleName.CHEF)), eq(null), eq("admin@example.com"));
  }

  @Test
  void updateRoles_promoteToAdminWithConfirmation_returns200() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateRolesRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateRolesRequest(
            Set.of(RoleName.CUSTOMER, RoleName.ADMIN),
            "PROMOVER USER@EXAMPLE.COM A ADMIN");

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(2L)
            .cognitoUserId("cognito-456")
            .email("user@example.com")
            .firstName("Test")
            .lastName("User")
            .phone("+59899222222")
            .roles(Set.of("CUSTOMER", "ADMIN"))
            .build();

    when(adminUserService.updateRoles(eq(2L), any(), any(), any())).thenReturn(mockUser);

    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/admin/users/2/roles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasItem("ADMIN")));

    verify(adminUserService, times(1))
        .updateRoles(
            eq(2L),
            eq(Set.of(RoleName.CUSTOMER, RoleName.ADMIN)),
            eq("PROMOVER USER@EXAMPLE.COM A ADMIN"),
            eq("admin@example.com"));
  }

  @Test
  void updateRoles_selfDemotion_returns400() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateRolesRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateRolesRequest(
            Set.of(RoleName.CUSTOMER), null);

    when(adminUserService.updateRoles(eq(1L), any(), any(), any()))
        .thenThrow(
            new com.cocinadelicia.backend.common.exception.BadRequestException(
                "SELF_DEMOTION_NOT_ALLOWED",
                "No puede quitarse a sí mismo el rol ADMIN"));

    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/admin/users/1/roles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("SELF_DEMOTION_NOT_ALLOWED"));

    verify(adminUserService, times(1)).updateRoles(eq(1L), any(), any(), any());
  }

  @Test
  void updateRoles_userNotFound_returns404() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateRolesRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateRolesRequest(
            Set.of(RoleName.CUSTOMER), null);

    when(adminUserService.updateRoles(eq(999L), any(), any(), any()))
        .thenThrow(new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado"));

    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/admin/users/999/roles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

    verify(adminUserService, times(1)).updateRoles(eq(999L), any(), any(), any());
  }

  @Test
  void updateRoles_emptyRoles_returns400() throws Exception {
    String requestBody = "{\"roles\": []}";

    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/admin/users/1/roles")
                .contentType("application/json")
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields").exists());

    verify(adminUserService, never()).updateRoles(any(), any(), any(), any());
  }

  // ==================== US06: PATCH /api/admin/users/{id}/status ====================

  @Test
  void updateStatus_withoutAuth_returns401() throws Exception {
    String requestBody = "{\"isActive\": true}";

    mvc.perform(
            patch("/api/admin/users/1/status")
                .contentType("application/json")
                .content(requestBody))
        .andExpect(status().isUnauthorized());

    verify(adminUserService, never()).updateStatus(any(), anyBoolean(), any());
  }

  @Test
  void updateStatus_withCustomerRole_returns403() throws Exception {
    String requestBody = "{\"isActive\": false}";

    mvc.perform(
            patch("/api/admin/users/1/status")
                .contentType("application/json")
                .content(requestBody)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());

    verify(adminUserService, never()).updateStatus(any(), anyBoolean(), any());
  }

  @Test
  void updateStatus_activateUser_returns200() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest(true);

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(1L)
            .cognitoUserId("cognito-123")
            .email("user@example.com")
            .firstName("Test")
            .lastName("User")
            .phone("+59899111111")
            .roles(Set.of("CUSTOMER"))
            .build();

    when(adminUserService.updateStatus(eq(1L), eq(true), any())).thenReturn(mockUser);

    mvc.perform(
            patch("/api/admin/users/1/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("user@example.com"));

    verify(adminUserService, times(1)).updateStatus(eq(1L), eq(true), eq("admin@example.com"));
  }

  @Test
  void updateStatus_deactivateUser_returns200() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest(false);

    UserResponseDTO mockUser =
        UserResponseDTO.builder()
            .id(2L)
            .cognitoUserId("cognito-456")
            .email("another@example.com")
            .firstName("Another")
            .lastName("User")
            .phone("+59899222222")
            .roles(Set.of("CHEF"))
            .build();

    when(adminUserService.updateStatus(eq(2L), eq(false), any())).thenReturn(mockUser);

    mvc.perform(
            patch("/api/admin/users/2/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2));

    verify(adminUserService, times(1)).updateStatus(eq(2L), eq(false), eq("admin@example.com"));
  }

  @Test
  void updateStatus_userNotFound_returns404() throws Exception {
    com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest request =
        new com.cocinadelicia.backend.user.dto.UpdateUserStatusRequest(true);

    when(adminUserService.updateStatus(eq(999L), anyBoolean(), any()))
        .thenThrow(new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado"));

    mvc.perform(
            patch("/api/admin/users/999/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

    verify(adminUserService, times(1)).updateStatus(eq(999L), eq(true), any());
  }

  @Test
  void updateStatus_missingIsActive_returns400() throws Exception {
    String requestBody = "{}"; // isActive null

    mvc.perform(
            patch("/api/admin/users/1/status")
                .contentType("application/json")
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("email", "admin@example.com"))
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields").exists());

    verify(adminUserService, never()).updateStatus(any(), anyBoolean(), any());
  }
}
