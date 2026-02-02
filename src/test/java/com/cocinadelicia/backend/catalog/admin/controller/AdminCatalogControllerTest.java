package com.cocinadelicia.backend.catalog.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.catalog.admin.service.AdminCategoryService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierGroupService;
import com.cocinadelicia.backend.catalog.admin.service.AdminModifierOptionService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductService;
import com.cocinadelicia.backend.catalog.admin.service.AdminProductVariantService;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.dto.ProductAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductAdminResponse;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import com.cocinadelicia.backend.product.dto.ProductVariantPriceUpdateRequest;
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

@WebMvcTest(controllers = AdminCatalogController.class)
@Import({
  AdminCatalogControllerTest.MockConfig.class,
  AdminCatalogControllerTest.MethodSecurityConfig.class
})
@TestPropertySource(
    properties = {
      "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:0/fake-jwks"
    })
class AdminCatalogControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Autowired AdminCategoryService categoryService;
  @Autowired AdminProductService productService;
  @Autowired AdminProductVariantService variantService;
  @Autowired AdminModifierGroupService modifierGroupService;
  @Autowired AdminModifierOptionService modifierOptionService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    AdminCategoryService categoryService() {
      return Mockito.mock(AdminCategoryService.class);
    }

    @Bean
    AdminProductService productService() {
      return Mockito.mock(AdminProductService.class);
    }

    @Bean
    AdminProductVariantService variantService() {
      return Mockito.mock(AdminProductVariantService.class);
    }

    @Bean
    AdminModifierGroupService modifierGroupService() {
      return Mockito.mock(AdminModifierGroupService.class);
    }

    @Bean
    AdminModifierOptionService modifierOptionService() {
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

  private ProductVariantAdminResponse sampleVariantResponse(Long id) {
    return new ProductVariantAdminResponse(id, "Variante", "SKU-1", true, true, 5);
  }

  private ProductAdminResponse sampleResponse(Long id) {
    return new ProductAdminResponse(
        id,
        10L,
        "Platos",
        "Milanesa",
        "milanesa",
        "Descripcion",
        new BigDecimal("22.00"),
        true,
        Instant.parse("2025-01-10T10:00:00Z"),
        2,
        true,
        false,
        false);
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(
        categoryService,
        productService,
        variantService,
        modifierGroupService,
        modifierOptionService);
  }

  @Test
  void getProducts_ok_returns200() throws Exception {
    var page = new PageResponse<>(List.of(sampleResponse(1L)), 0, 20, 1, 1);
    Mockito.when(productService.getProducts(any(), any(), any())).thenReturn(page);

    mvc.perform(get("/api/admin/catalog/products").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1));
  }

  @Test
  void getProducts_withFilters_returns200() throws Exception {
    var page = new PageResponse<>(List.of(sampleResponse(2L)), 0, 20, 1, 1);
    Mockito.when(productService.getProducts(eq(2L), eq(true), any())).thenReturn(page);

    mvc.perform(
            get("/api/admin/catalog/products")
                .param("categoryId", "2")
                .param("isActive", "true")
                .with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(2));
  }

  @Test
  void getProduct_ok_returns200() throws Exception {
    Mockito.when(productService.getById(5L)).thenReturn(sampleResponse(5L));

    mvc.perform(get("/api/admin/catalog/products/5").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.slug").value("milanesa"));
  }

  @Test
  void getProduct_notFound_returns404() throws Exception {
    Mockito.when(productService.getById(999L))
        .thenThrow(new NotFoundException("PRODUCT_NOT_FOUND", "No existe"));

    mvc.perform(get("/api/admin/catalog/products/999").with(adminJwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  void putProduct_ok_returns200() throws Exception {
    var request =
        new ProductAdminRequest(
            10L,
            "Milanesa",
            "milanesa",
            "Descripcion",
            new BigDecimal("22.00"),
            true,
            false,
            false,
            false);
    Mockito.when(productService.update(eq(5L), any())).thenReturn(sampleResponse(5L));

    mvc.perform(
            put("/api/admin/catalog/products/5")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5));
  }

  @Test
  void putProduct_validationError_returns400() throws Exception {
    String body =
        """
        {
          "name": "A",
          "slug": "x",
          "taxRatePercent": -1
        }
        """;

    mvc.perform(
            put("/api/admin/catalog/products/5")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.name").exists())
        .andExpect(jsonPath("$.fields.slug").exists())
        .andExpect(jsonPath("$.fields.categoryId").exists())
        .andExpect(jsonPath("$.fields.isActive").exists())
        .andExpect(jsonPath("$.fields.isFeatured").exists())
        .andExpect(jsonPath("$.fields.isNew").exists())
        .andExpect(jsonPath("$.fields.isDailyMenu").exists());
  }

  @Test
  void putProduct_categoryNotFound_returns400() throws Exception {
    var request =
        new ProductAdminRequest(
            999L,
            "Milanesa",
            "milanesa",
            "Descripcion",
            new BigDecimal("22.00"),
            true,
            false,
            false,
            false);
    Mockito.when(productService.update(eq(5L), any()))
        .thenThrow(new BadRequestException("CATEGORY_NOT_FOUND", "No existe"));

    mvc.perform(
            put("/api/admin/catalog/products/5")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  void putProduct_slugDuplicate_returns400() throws Exception {
    var request =
        new ProductAdminRequest(
            10L,
            "Milanesa",
            "milanesa",
            "Descripcion",
            new BigDecimal("22.00"),
            true,
            false,
            false,
            false);
    Mockito.when(productService.update(eq(5L), any()))
        .thenThrow(new BadRequestException("PRODUCT_SLUG_DUPLICATE", "Duplicado"));

    mvc.perform(
            put("/api/admin/catalog/products/5")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PRODUCT_SLUG_DUPLICATE"));
  }

  @Test
  void putVariantPrice_ok_returns200() throws Exception {
    var request = new ProductVariantPriceUpdateRequest(new BigDecimal("250.00"), null);
    Mockito.when(variantService.updateActivePrice(eq(12L), any()))
        .thenReturn(sampleVariantResponse(1L));

    mvc.perform(
            put("/api/admin/catalog/variants/12/price")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void putVariantPrice_validationError_returns400() throws Exception {
    mvc.perform(
            put("/api/admin/catalog/variants/12/price")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.price").exists());
  }

  @Test
  void putVariantPrice_notFound_returns404() throws Exception {
    var request = new ProductVariantPriceUpdateRequest(new BigDecimal("250.00"), null);
    Mockito.when(variantService.updateActivePrice(eq(999L), any()))
        .thenThrow(new NotFoundException("VARIANT_NOT_FOUND", "No existe"));

    mvc.perform(
            put("/api/admin/catalog/variants/999/price")
                .with(adminJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("VARIANT_NOT_FOUND"));
  }

  @Test
  void getProducts_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/admin/catalog/products")).andExpect(status().isUnauthorized());
  }

  @Test
  void getProducts_withoutAdminRole_returns403() throws Exception {
    mvc.perform(get("/api/admin/catalog/products").with(noRoleJwt()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getModifierGroups_ok_returns200() throws Exception {
    Mockito.when(modifierGroupService.getByProduct(10L)).thenReturn(List.of());

    mvc.perform(get("/api/admin/catalog/products/10/modifier-groups").with(adminJwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getModifierGroups_notFound_returns404() throws Exception {
    Mockito.when(modifierGroupService.getByProduct(999L))
        .thenThrow(new NotFoundException("PRODUCT_NOT_FOUND", "No existe"));

    mvc.perform(get("/api/admin/catalog/products/999/modifier-groups").with(adminJwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  void getModifierGroups_withoutToken_returns401() throws Exception {
    mvc.perform(get("/api/admin/catalog/products/10/modifier-groups"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getModifierGroups_withoutAdminRole_returns403() throws Exception {
    mvc.perform(get("/api/admin/catalog/products/10/modifier-groups").with(noRoleJwt()))
        .andExpect(status().isForbidden());
  }
}
