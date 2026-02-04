package com.cocinadelicia.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.config.SecurityConfig;
import com.cocinadelicia.backend.common.exception.ConflictException;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.dto.InviteUserRequest;
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
}
