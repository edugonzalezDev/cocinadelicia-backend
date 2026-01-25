// src/test/java/com/cocinadelicia/backend/catalog/CatalogControllerTest.java
package com.cocinadelicia.backend.catalog;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cocinadelicia.backend.product.model.*;
import com.cocinadelicia.backend.product.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CatalogControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductVariantRepository variantRepository;
  @Autowired private PriceHistoryRepository priceHistoryRepository;
  @Autowired private TagRepository tagRepository;

  private Category category;
  private Product product1;
  private Product product2;
  private Product product3;
  private Tag tagPicante;

  @BeforeEach
  void setUp() {
    // Crear categoría
    category =
        Category.builder()
            .name("Empanadas")
            .slug("empanadas")
            .description("Empanadas caseras")
            .build();
    categoryRepository.save(category);

    // Crear tag
    tagPicante = Tag.builder().name("Picante").slug("picante").build();
    tagRepository.save(tagPicante);

    // Producto 1: Milanesa con variante disponible y destacada
    product1 =
        Product.builder()
            .category(category)
            .name("Milanesa napolitana")
            .slug("milanesa-napolitana")
            .description("Deliciosa milanesa con salsa y queso")
            .taxRatePercent(BigDecimal.valueOf(10.5))
            .isActive(true)
            .build();
    productRepository.save(product1);

    ProductVariant variant1 =
        ProductVariant.builder()
            .product(product1)
            .name("Porción individual")
            .sku("MIL-NAP-001")
            .isActive(true)
            .managesStock(true)
            .stockQuantity(10)
            .featured(true)
            .dailyMenu(false)
            .isNew(false)
            .build();
    variantRepository.save(variant1);

    PriceHistory price1 =
        PriceHistory.builder()
            .productVariant(variant1)
            .price(BigDecimal.valueOf(850.00))
            .currency(com.cocinadelicia.backend.common.model.enums.CurrencyCode.UYU)
            .validFrom(Instant.now().minusSeconds(3600))
            .build();
    priceHistoryRepository.save(price1);

    // Producto 2: Empanada con variante sin stock (managesStock=true, stock=0)
    product2 =
        Product.builder()
            .category(category)
            .name("Empanada de carne")
            .slug("empanada-carne")
            .description("Empanada tradicional de carne suave")
            .taxRatePercent(BigDecimal.valueOf(10.5))
            .isActive(true)
            .build();
    product2.getTags().add(tagPicante);
    productRepository.save(product2);

    ProductVariant variant2 =
        ProductVariant.builder()
            .product(product2)
            .name("Docena")
            .sku("EMP-CARNE-12")
            .isActive(true)
            .managesStock(true)
            .stockQuantity(0)
            .featured(false)
            .dailyMenu(true)
            .isNew(false)
            .build();
    variantRepository.save(variant2);

    PriceHistory price2 =
        PriceHistory.builder()
            .productVariant(variant2)
            .price(BigDecimal.valueOf(1200.00))
            .currency(com.cocinadelicia.backend.common.model.enums.CurrencyCode.UYU)
            .validFrom(Instant.now().minusSeconds(3600))
            .build();
    priceHistoryRepository.save(price2);

    // Producto 3: Pizza nueva con variante disponible (managesStock=false)
    Category pizzaCategory =
        Category.builder().name("Pizzas").slug("pizzas").description("Pizzas artesanales").build();
    categoryRepository.save(pizzaCategory);

    product3 =
        Product.builder()
            .category(pizzaCategory)
            .name("Pizza muzzarella")
            .slug("pizza-muzzarella")
            .description("Pizza casera con extra queso")
            .taxRatePercent(BigDecimal.valueOf(10.5))
            .isActive(true)
            .build();
    productRepository.save(product3);

    ProductVariant variant3 =
        ProductVariant.builder()
            .product(product3)
            .name("Grande")
            .sku("PIZ-MUZ-L")
            .isActive(true)
            .managesStock(false)
            .stockQuantity(0)
            .featured(false)
            .dailyMenu(false)
            .isNew(true)
            .build();
    variantRepository.save(variant3);

    PriceHistory price3 =
        PriceHistory.builder()
            .productVariant(variant3)
            .price(BigDecimal.valueOf(1500.00))
            .currency(com.cocinadelicia.backend.common.model.enums.CurrencyCode.UYU)
            .validFrom(Instant.now().minusSeconds(3600))
            .build();
    priceHistoryRepository.save(price3);
  }

  @Test
  void testGetProducts_WithoutFilters_ReturnsAllActiveProducts() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(3)))
        .andExpect(jsonPath("$.totalElements", is(3)))
        .andExpect(jsonPath("$.content[*].name", hasItems("Milanesa napolitana", "Empanada de carne", "Pizza muzzarella")));
  }

  @Test
  void testGetProducts_WithSearchQuery_FindsByName() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("q", "milanesa"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Milanesa napolitana")));
  }

  @Test
  void testGetProducts_WithSearchQuery_FindsByDescription() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("q", "extra queso"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Pizza muzzarella")));
  }

  @Test
  void testGetProducts_WithSearchQuery_FindsByTag() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("q", "picante"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Empanada de carne")));
  }

  @Test
  void testGetProducts_WithCategorySlug_FiltersCorrectly() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("categorySlug", "empanadas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[*].name", hasItems("Milanesa napolitana", "Empanada de carne")));
  }

  @Test
  void testGetProducts_WithAvailableOnly_ExcludesOutOfStock() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("availableOnly", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(
            jsonPath("$.content[*].name", hasItems("Milanesa napolitana", "Pizza muzzarella")))
        .andExpect(jsonPath("$.content[*].name", not(hasItem("Empanada de carne"))));
  }

  @Test
  void testGetProducts_WithFeatured_ReturnsOnlyFeaturedProducts() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("featured", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Milanesa napolitana")))
        .andExpect(jsonPath("$.content[0].featured", is(true)));
  }

  @Test
  void testGetProducts_WithDailyMenu_ReturnsOnlyDailyMenuProducts() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("dailyMenu", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Empanada de carne")))
        .andExpect(jsonPath("$.content[0].dailyMenu", is(true)));
  }

  @Test
  void testGetProducts_WithNew_ReturnsOnlyNewProducts() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("new", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Pizza muzzarella")))
        .andExpect(jsonPath("$.content[0].new", is(true)));
  }

  @Test
  void testGetProducts_CombinedFilters_SearchAndCategory() throws Exception {
    mockMvc
        .perform(
            get("/api/catalog/products")
                .param("q", "empanada")
                .param("categorySlug", "empanadas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Empanada de carne")));
  }

  @Test
  void testGetProducts_CombinedFilters_CategoryAndAvailableOnly() throws Exception {
    mockMvc
        .perform(
            get("/api/catalog/products")
                .param("categorySlug", "empanadas")
                .param("availableOnly", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Milanesa napolitana")));
  }

  @Test
  void testGetProducts_CombinedFilters_SearchAndAvailableOnlyAndFeatured() throws Exception {
    mockMvc
        .perform(
            get("/api/catalog/products")
                .param("q", "milanesa")
                .param("availableOnly", "true")
                .param("featured", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Milanesa napolitana")))
        .andExpect(jsonPath("$.content[0].featured", is(true)))
        .andExpect(jsonPath("$.content[0].available", is(true)));
  }

  @Test
  void testGetProducts_Pagination_WorksCorrectly() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("page", "0").param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.totalElements", is(3)))
        .andExpect(jsonPath("$.totalPages", is(2)));
  }

  @Test
  void testGetProducts_NoResults_ReturnsEmptyPage() throws Exception {
    mockMvc
        .perform(get("/api/catalog/products").param("q", "inexistente"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.totalElements", is(0)));
  }
}
