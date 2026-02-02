package com.cocinadelicia.backend.catalog.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.catalog.admin.service.AdminModifierGroupService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierOptionService;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminResponse;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminModifierController.class)
@Import({
  AdminCatalogModifierControllerTest.MockConfig.class,
  AdminCatalogModifierControllerTest.MethodSecurityConfig.class
})
@TestPropertySource(
    properties = {
      "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:0/fake-jwks"
    })
class AdminCatalogModifierControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Autowired AdminModifierGroupService groupService;
  @Autowired AdminModifierOptionService optionService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    AdminModifierGroupService groupService() {
      return Mockito.mock(AdminModifierGroupService.class);
    }

    @Bean
    AdminModifierOptionService optionService() {
      return Mockito.mock(AdminModifierOptionService.class);
    }

    @Bean
    SpringDocConfiguration springDocConfiguration() {
      return new SpringDocConfiguration();
    }

    @Bean
    JwtDecoder jwtDecoder() {
      return Mockito.mock(JwtDecoder.class);
    }
  }

  @TestConfiguration
  @EnableMethodSecurity
  static class MethodSecurityConfig {}

  private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
    return jwt()
        .jwt(
            jwt -> {
              jwt.subject("sub-123");
              jwt.claim("email", "admin@test.com");
            })
        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor noRoleJwt() {
    return jwt()
        .jwt(
            jwt -> {
              jwt.subject("sub-123");
              jwt.claim("email", "user@test.com");
            })
        .authorities();
  }

  private ModifierGroupAdminResponse sampleGroup(Long id) {
    return new ModifierGroupAdminResponse(
        id, 10L, "Guarniciones", 0, 2, "MULTI", null, null, 0, true, List.of());
  }

  private ModifierOptionAdminResponse sampleOption(Long id) {
    return new ModifierOptionAdminResponse(
        id, 20L, "Pure", 0, true, new BigDecimal("10.00"), false, null);
  }

  @Test
  void listByVariant_ok_returns200() throws Exception {
    Mockito.when(groupService.getByVariant(10L)).thenReturn(List.of(sampleGroup(1L)));

    mvc.perform(get("/api/admin/modifier-groups").param("productVariantId", "10").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void createGroup_ok_returns200() throws Exception {
    var request =
        new ModifierGroupAdminRequest(10L, "Guarniciones", 0, 2, "MULTI", null, null, 0, true);
    Mockito.when(groupService.create(any())).thenReturn(sampleGroup(1L));

    mvc.perform(
            post("/api/admin/modifier-groups")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void listOptions_ok_returns200() throws Exception {
    Mockito.when(optionService.getByGroup(20L)).thenReturn(List.of(sampleOption(5L)));

    mvc.perform(get("/api/admin/modifier-groups/20/options").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(5));
  }

  @Test
  void createOption_ok_returns200() throws Exception {
    var request =
        new ModifierOptionAdminRequest("Pure", 0, true, new BigDecimal("10.00"), false, null);
    Mockito.when(optionService.create(eq(20L), any())).thenReturn(sampleOption(5L));

    mvc.perform(
            post("/api/admin/modifier-groups/20/options")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5));
  }

  @Test
  void listByVariant_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/admin/modifier-groups").param("productVariantId", "10"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void listByVariant_withoutAdminRole_returns403() throws Exception {
    mvc.perform(get("/api/admin/modifier-groups").param("productVariantId", "10").with(noRoleJwt()))
        .andExpect(status().isForbidden());
  }
}
