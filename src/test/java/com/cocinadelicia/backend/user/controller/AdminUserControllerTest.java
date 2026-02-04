package com.cocinadelicia.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.config.SecurityConfig;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.service.AdminUserService;
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
}
