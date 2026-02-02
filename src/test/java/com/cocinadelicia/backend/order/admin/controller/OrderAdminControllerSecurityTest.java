package com.cocinadelicia.backend.order.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderStatusAdminRequest;
import com.cocinadelicia.backend.order.admin.service.OrderAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderAdminController.class)
@Import({
  OrderAdminControllerSecurityTest.MockConfig.class,
  com.cocinadelicia.backend.common.config.SecurityConfig.class
})
@TestPropertySource(
    properties = {
      "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:0/fake-jwks"
    })
class OrderAdminControllerSecurityTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper om;

  @TestConfiguration
  static class MockConfig {
    @Bean
    OrderAdminService orderAdminService() {
      return Mockito.mock(OrderAdminService.class);
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

  private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor noRoleJwt() {
    return SecurityMockMvcRequestPostProcessors.jwt()
        .jwt(
            jwt -> {
              jwt.subject("sub-abc");
              jwt.claim("email", "user@test.com");
            })
        .authorities();
  }

  @Test
  void patchStatus_withoutToken_returns401() throws Exception {
    var body = new UpdateOrderStatusAdminRequest(OrderStatus.CONFIRMED, "ok");

    mvc.perform(
            patch("/api/admin/orders/10/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void patchStatus_withNoRole_returns403() throws Exception {
    var body = new UpdateOrderStatusAdminRequest(OrderStatus.CONFIRMED, "ok");

    mvc.perform(
            patch("/api/admin/orders/10/status")
                .with(noRoleJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isForbidden());
  }
}
