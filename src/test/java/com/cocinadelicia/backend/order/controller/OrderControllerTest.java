// src/test/java/com/cocinadelicia/backend/order/controller/OrderControllerTest.java
package com.cocinadelicia.backend.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(controllers = OrderController.class)
@Import({OrderControllerTest.MockConfig.class, OrderControllerTest.MethodSecurityConfig.class})
@TestPropertySource(
    properties = {"spring.security.oauth2.resourceserver.jwt.issuer-uri=disabled-for-tests"})
class OrderControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;
  @Autowired OrderService orderService;
  @Autowired UserService userService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    OrderService orderService() {
      return Mockito.mock(OrderService.class);
    }

    @Bean
    UserService userService() {
      return Mockito.mock(UserService.class);
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

  // ✅ Habilita @PreAuthorize en este slice de prueba
  @TestConfiguration
  @EnableMethodSecurity
  static class MethodSecurityConfig {}

  // ✅ Admin con autoridad real
  private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
    return jwt()
        .jwt(
            jwt -> {
              jwt.subject("sub-123");
              jwt.claim("email", "admin@test.com");
            })
        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  // ✅ Sin roles/autorizaciones
  private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor noRoleJwt() {
    return jwt()
        .jwt(
            jwt -> {
              jwt.subject("sub-abc");
              jwt.claim("email", "user@test.com");
            })
        .authorities(); // vacío
  }

  /**
   * IMPORTANTE: OrderResponse (record) ahora requiere también: - createdAt, updatedAt - items
   * (List<OrderItemResponse>) - requestedAt, deliveredAt
   *
   * <p>(esto viene del error de compilación en Render)
   */
  private OrderResponse sampleResponse(Long id, OrderStatus status) {
    Instant now = Instant.now();

    // Tipado explícito para evitar que List.of() infiera List<Object>
    List<OrderItemResponse> items = List.of();

    // requestedAt / deliveredAt (demo)
    Instant requestedAt = now;
    Instant deliveredAt = null;

    return new OrderResponse(
        id,
        status,
        FulfillmentType.PICKUP,
        CurrencyCode.UYU,
        new BigDecimal("100.00"), // subtotal
        BigDecimal.ZERO, // tax
        BigDecimal.ZERO, // discount
        new BigDecimal("100.00"), // total
        "Eduardo", // shipName
        "099", // shipPhone
        "Calle 1", // shipLine1
        null, // shipLine2
        "MVD", // shipCity
        null, // shipRegion
        null, // shipPostalCode
        null, // shipReference
        "notes", // notes
        now, // createdAt
        now, // updatedAt
        items, // items
        requestedAt, // requestedAt
        deliveredAt // deliveredAt
        );
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(orderService, userService);
  }

  @Test
  void patchStatus_ok_returns200() throws Exception {
    Mockito.when(userService.resolveUserIdFromJwt(any())).thenReturn(1L);
    Mockito.when(orderService.updateStatus(eq(10L), anyString(), any()))
        .thenReturn(sampleResponse(10L, OrderStatus.PREPARING));

    var body = new UpdateOrderStatusRequest(OrderStatus.PREPARING, "nota");

    mvc.perform(
            patch("/api/orders/10/status")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.status").value("PREPARING"));
  }

  @Test
  void patchStatus_notFound_returns404() throws Exception {
    Mockito.when(orderService.updateStatus(eq(999L), anyString(), any()))
        .thenThrow(new NotFoundException("ORDER_NOT_FOUND", "Pedido no encontrado."));

    var body = new UpdateOrderStatusRequest(OrderStatus.PREPARING, null);

    mvc.perform(
            patch("/api/orders/999/status")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
  }

  @Test
  void patchStatus_invalidTransition_returns400() throws Exception {
    Mockito.when(orderService.updateStatus(eq(10L), anyString(), any()))
        .thenThrow(
            new BadRequestException(
                "INVALID_STATUS_TRANSITION", "No se puede pasar de CREATED a DELIVERED"));

    var body = new UpdateOrderStatusRequest(OrderStatus.DELIVERED, null);

    mvc.perform(
            patch("/api/orders/10/status")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
  }

  @Test
  void patchStatus_missingStatus_returns400() throws Exception {
    Mockito.when(orderService.updateStatus(eq(10L), anyString(), any()))
        .thenThrow(new BadRequestException("STATUS_REQUIRED", "El estado es obligatorio."));

    var body = new UpdateOrderStatusRequest(null, "nota");

    mvc.perform(
            patch("/api/orders/10/status")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("STATUS_REQUIRED"));
  }

  @Test
  void patchStatus_withoutToken_returns401() throws Exception {
    var body = new UpdateOrderStatusRequest(OrderStatus.PREPARING, "nota");

    mvc.perform(
            patch("/api/orders/10/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void patchStatus_withNoRequiredRole_returns403() throws Exception {
    var body = new UpdateOrderStatusRequest(OrderStatus.PREPARING, "nota");

    mvc.perform(
            patch("/api/orders/10/status")
                .with(noRoleJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(body)))
        .andExpect(status().isForbidden());
  }
}
