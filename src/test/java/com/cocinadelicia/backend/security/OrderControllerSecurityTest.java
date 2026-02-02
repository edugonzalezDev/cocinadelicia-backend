package com.cocinadelicia.backend.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.config.SecurityConfig;
import com.cocinadelicia.backend.order.controller.OrderController;
import com.cocinadelicia.backend.order.domain.OrderOwnershipValidator;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import com.cocinadelicia.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {"cdd.security.groups-claim=cognito:groups"})
@ActiveProfiles("test")
class OrderControllerSecurityTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private OrderService orderService;
  @MockitoBean private UserService userService;
  @MockitoBean private CurrentUserService currentUserService;
  @MockitoBean private OrderOwnershipValidator orderOwnershipValidator;
  @MockitoBean private JwtDecoder jwtDecoder;

  @BeforeEach
  void setup() {
    Mockito.reset(orderService, userService, currentUserService);
    Mockito.when(orderService.findOrders(Mockito.any(), Mockito.any())).thenReturn(Page.empty());
  }

  @Test
  void backoffice_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/orders/admin")).andExpect(status().isUnauthorized());
  }

  @Test
  void backoffice_withCustomer_returns403() throws Exception {
    mvc.perform(
            get("/api/orders/admin")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void backoffice_withChef_returns200() throws Exception {
    mvc.perform(
            get("/api/orders/admin")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CHEF"))))
        .andExpect(status().isOk());
  }
}
