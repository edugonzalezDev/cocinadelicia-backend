package com.cocinadelicia.backend.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.catalog.admin.controller.AdminCatalogController;
import com.cocinadelicia.backend.catalog.admin.service.AdminCategoryService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierGroupService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierOptionService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductVariantService;
import com.cocinadelicia.backend.common.config.SecurityConfig;
import com.cocinadelicia.backend.common.web.PageResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminCatalogController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {"cdd.security.groups-claim=cognito:groups"})
class AdminCatalogSecurityTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private AdminCategoryService categoryService;
  @MockitoBean private AdminProductService productService;
  @MockitoBean private AdminProductVariantService variantService;
  @MockitoBean private AdminModifierGroupService modifierGroupService;
  @MockitoBean private AdminModifierOptionService modifierOptionService;

  @BeforeEach
  void setupMocks() {
    Mockito.reset(
        categoryService,
        productService,
        variantService,
        modifierGroupService,
        modifierOptionService);
    Mockito.when(productService.getProducts(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));
  }

  @Test
  void adminEndpoint_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/admin/catalog/products")).andExpect(status().isUnauthorized());
  }

  @Test
  void adminEndpoint_withChefToken_returns403() throws Exception {
    mvc.perform(
            get("/api/admin/catalog/products")
                .with(jwt().jwt(jwt -> jwt.claim("cognito:groups", List.of("CHEF")))))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminEndpoint_withAdminToken_returns200() throws Exception {
    mvc.perform(
            get("/api/admin/catalog/products")
                .with(jwt().jwt(jwt -> jwt.claim("cognito:groups", List.of("ADMIN")))))
        .andExpect(status().isOk());
  }
}
