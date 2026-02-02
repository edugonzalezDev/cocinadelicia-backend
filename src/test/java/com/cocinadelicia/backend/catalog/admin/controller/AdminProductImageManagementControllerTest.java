package com.cocinadelicia.backend.catalog.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.catalog.admin.dto.PresignImageUploadRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminResponse;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductImageService;
import com.cocinadelicia.backend.common.s3.ImagePresignService;
import com.cocinadelicia.backend.common.s3.ImagePresignService.PresignResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(
    controllers = {AdminProductImageManagementController.class, AdminProductImageController.class})
@Import({
  AdminProductImageManagementControllerTest.MockConfig.class,
  AdminProductImageManagementControllerTest.MethodSecurityConfig.class
})
@TestPropertySource(
    properties = {
      "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:0/fake-jwks"
    })
class AdminProductImageManagementControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Autowired AdminProductImageService imageService;
  @Autowired ImagePresignService imagePresignService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    AdminProductImageService imageService() {
      return mock(AdminProductImageService.class);
    }

    @Bean
    ImagePresignService imagePresignService() {
      return mock(ImagePresignService.class);
    }

    @Bean
    SpringDocConfiguration springDocConfiguration() {
      return new SpringDocConfiguration();
    }

    @Bean
    JwtDecoder jwtDecoder() {
      return mock(JwtDecoder.class);
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

  @Test
  void presign_ok_returns200() throws Exception {
    var req = new PresignImageUploadRequest("image/png", 1000);
    when(imagePresignService.presignProductImageUpload(eq(10L), eq("image/png")))
        .thenReturn(
            new PresignResult(
                "http://upload",
                "products/10/abc.png",
                "http://cdn/products/10/abc.png",
                Map.of("Content-Type", "image/png")));

    mvc.perform(
            post("/api/admin/catalog/products/10/images/presign")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objectKey").value("products/10/abc.png"));
  }

  @Test
  void list_ok_returns200_inOrder() throws Exception {
    when(imageService.listByProduct(10L))
        .thenReturn(
            List.of(
                new ProductImageAdminResponse(1L, "k1", "u1", true, 0),
                new ProductImageAdminResponse(2L, "k2", "u2", false, 2)));

    mvc.perform(get("/api/admin/catalog/products/10/images").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));
  }

  @Test
  void add_ok_returns201() throws Exception {
    var req = new ProductImageAdminRequest("products/10/abc.png", true, 0);
    when(imageService.addToProduct(eq(10L), any()))
        .thenReturn(new ProductImageAdminResponse(10L, "k", "u", true, 0));

    mvc.perform(
            post("/api/admin/catalog/products/10/images")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/admin/catalog/images/10"))
        .andExpect(jsonPath("$.id").value(10));
  }

  @Test
  void patch_ok_returns200() throws Exception {
    when(imageService.patch(eq(10L), any()))
        .thenReturn(new ProductImageAdminResponse(10L, "k", "u", true, 1));

    mvc.perform(
            patch("/api/admin/catalog/images/10")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isMain\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isMain").value(true));
  }

  @Test
  void delete_ok_returns204() throws Exception {
    mvc.perform(delete("/api/admin/catalog/images/10").with(adminJwt()))
        .andExpect(status().isNoContent());
  }

  @Test
  void list_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/admin/catalog/products/10/images")).andExpect(status().isUnauthorized());
  }

  @Test
  void list_withoutAdminRole_returns403() throws Exception {
    mvc.perform(get("/api/admin/catalog/products/10/images").with(noRoleJwt()))
        .andExpect(status().isForbidden());
  }
}
