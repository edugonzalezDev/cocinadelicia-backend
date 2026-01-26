package com.cocinadelicia.backend.catalog.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cocinadelicia.backend.catalog.dto.CatalogImageResponse;
import com.cocinadelicia.backend.catalog.dto.CatalogVariantResponse;
import com.cocinadelicia.backend.catalog.dto.MoneyResponse;
import com.cocinadelicia.backend.catalog.dto.ProductDetailResponse;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;

@WebMvcTest(controllers = CatalogController.class)
@Import({CatalogDetailEndpointsTest.MockConfig.class})
@TestPropertySource(
    properties = {
      "spring.security.oauth2.resourceserver.jwt.issuer-uri=disabled-for-tests",
      "app.cdn.base-url=https://cdn.test/"
    })
class CatalogDetailEndpointsTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @Autowired CatalogService catalogService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    CatalogService catalogService() {
      return Mockito.mock(CatalogService.class);
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

  @BeforeEach
  void resetMocks() {
    Mockito.reset(catalogService);
  }

  @Test
  void getProductBySlug_ok_returns200() throws Exception {
    var response = sampleDetail();
    Mockito.when(catalogService.getProductBySlug(eq("milanesa-clasica"))).thenReturn(response);

    mvc.perform(get("/api/catalog/products/milanesa-clasica").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slug").value("milanesa-clasica"))
        .andExpect(jsonPath("$.name").value("Milanesa clásica"))
        .andExpect(jsonPath("$.mainImageUrl").value("https://cdn.test/products/main.jpg"))
        .andExpect(jsonPath("$.images[0].url").value("https://cdn.test/products/main.jpg"))
        .andExpect(jsonPath("$.variants[0].price.amount").value(590))
        .andExpect(jsonPath("$.tags[0]").value("casero"));
  }

  @Test
  void getProductBySlug_notFound_returns404() throws Exception {
    Mockito.when(catalogService.getProductBySlug(eq("missing")))
        .thenThrow(new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado."));

    mvc.perform(get("/api/catalog/products/missing").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }

  private ProductDetailResponse sampleDetail() {
    var price = new MoneyResponse(new BigDecimal("590"), "UYU");
    var variant =
        new CatalogVariantResponse(100L, "Grande", price, true, 12, "Disponible");
    var image = new CatalogImageResponse(1L, "https://cdn.test/products/main.jpg", true, 0, "Milanesa clásica");
    return new ProductDetailResponse(
        10L,
        "milanesa-clasica",
        "Milanesa clásica",
        "Descripción larga del producto",
        "Crujiente y deliciosa",
        1L,
        "Platos",
        "platos",
        "https://cdn.test/products/main.jpg",
        List.of(image),
        price,
        List.of(variant),
        List.of("casero", "sin-tacc"),
        true,
        false,
        false,
        true,
        true,
        false);
  }
}
