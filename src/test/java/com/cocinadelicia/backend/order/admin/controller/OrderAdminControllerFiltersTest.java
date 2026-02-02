package com.cocinadelicia.backend.order.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderItemRepository;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.flyway.enabled=false"})
@WithMockUser(roles = "ADMIN")
class OrderAdminControllerFiltersTest {

  private static final ZoneId ZONE = ZoneId.of("America/Montevideo");

  @Autowired private MockMvc mockMvc;
  @Autowired private CustomerOrderRepository orderRepository;
  @Autowired private OrderItemRepository orderItemRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductVariantRepository productVariantRepository;

  @BeforeEach
  void setUp() {
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
    productVariantRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void getAllOrders_filtersByFulfillment() throws Exception {
    AppUser user = createUser("fulfillment@test.com");
    ProductVariant variant = createProductWithVariant("Pizza");
    CustomerOrder deliveryOrder =
        createOrder(
            user,
            FulfillmentType.DELIVERY,
            LocalDate.of(2026, 1, 5).atStartOfDay(ZONE).toInstant(),
            "nota",
            "099123",
            variant.getProduct(),
            variant);
    createOrder(
        user,
        FulfillmentType.PICKUP,
        LocalDate.of(2026, 1, 6).atStartOfDay(ZONE).toInstant(),
        "nota",
        "099999",
        variant.getProduct(),
        variant);

    mockMvc
        .perform(get("/api/admin/orders").param("fulfillment", "DELIVERY"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(deliveryOrder.getId()))
        .andExpect(jsonPath("$.content[0].fulfillment").value("DELIVERY"));
  }

  @Test
  void getAllOrders_filtersByRequestedAtRange() throws Exception {
    AppUser user = createUser("requested@test.com");
    ProductVariant variant = createProductWithVariant("Empanada");
    CustomerOrder inRange =
        createOrder(
            user,
            FulfillmentType.DELIVERY,
            LocalDate.of(2026, 2, 5).atStartOfDay(ZONE).toInstant(),
            "nota",
            "099111",
            variant.getProduct(),
            variant);
    createOrder(
        user,
        FulfillmentType.DELIVERY,
        LocalDate.of(2026, 3, 1).atStartOfDay(ZONE).toInstant(),
        "nota",
        "099222",
        variant.getProduct(),
        variant);

    mockMvc
        .perform(
            get("/api/admin/orders")
                .param("requestedAfter", "2026-02-01")
                .param("requestedBefore", "2026-02-10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(inRange.getId()));
  }

  @Test
  void getAllOrders_filtersByQNotesOrPhone() throws Exception {
    AppUser user = createUser("q@test.com");
    ProductVariant variant = createProductWithVariant("Tarta");
    CustomerOrder match =
        createOrder(
            user,
            FulfillmentType.DELIVERY,
            Instant.now(),
            "Sin Gluten",
            "099333",
            variant.getProduct(),
            variant);
    createOrder(
        user,
        FulfillmentType.DELIVERY,
        Instant.now(),
        "Normal",
        "099444",
        variant.getProduct(),
        variant);

    mockMvc
        .perform(get("/api/admin/orders").param("q", "gluten"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(match.getId()));
  }

  @Test
  void getAllOrders_filtersByProductId() throws Exception {
    AppUser user = createUser("product@test.com");
    ProductVariant variantA = createProductWithVariant("Sopa");
    ProductVariant variantB = createProductWithVariant("Ensalada");
    CustomerOrder withProductA =
        createOrder(
            user,
            FulfillmentType.PICKUP,
            Instant.now(),
            "nota",
            "099555",
            variantA.getProduct(),
            variantA);
    createOrder(
        user,
        FulfillmentType.PICKUP,
        Instant.now(),
        "nota",
        "099666",
        variantB.getProduct(),
        variantB);

    mockMvc
        .perform(
            get("/api/admin/orders")
                .param("productId", String.valueOf(variantA.getProduct().getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(withProductA.getId()));
  }

  @Test
  void getAllOrders_filtersByProductVariantId() throws Exception {
    AppUser user = createUser("variant@test.com");
    ProductVariant variantA = createProductWithVariant("Milanesa");
    ProductVariant variantB = createProductVariant(variantA.getProduct(), "Milanesa XL");
    CustomerOrder withVariantB =
        createOrder(
            user,
            FulfillmentType.PICKUP,
            Instant.now(),
            "nota",
            "099777",
            variantB.getProduct(),
            variantB);
    createOrder(
        user,
        FulfillmentType.PICKUP,
        Instant.now(),
        "nota",
        "099888",
        variantA.getProduct(),
        variantA);

    mockMvc
        .perform(
            get("/api/admin/orders").param("productVariantId", String.valueOf(variantB.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].id").value(withVariantB.getId()));
  }

  private AppUser createUser(String email) {
    AppUser user =
        AppUser.builder()
            .cognitoUserId("cognito-" + email)
            .firstName("Test")
            .lastName("User")
            .email(email)
            .phone("099000")
            .isActive(true)
            .build();
    return userRepository.save(user);
  }

  private ProductVariant createProductWithVariant(String name) {
    Category category =
        categoryRepository.save(
            Category.builder().name(name + " Cat").slug(name.toLowerCase() + "-cat").build());
    Product product =
        productRepository.save(
            Product.builder()
                .name(name)
                .slug(name.toLowerCase() + "-slug")
                .description("desc")
                .category(category)
                .taxRatePercent(new BigDecimal("0.00"))
                .isActive(true)
                .build());
    return createProductVariant(product, name + " Base");
  }

  private ProductVariant createProductVariant(Product product, String name) {
    ProductVariant variant =
        ProductVariant.builder()
            .product(product)
            .name(name)
            .sku(name.toLowerCase() + "-sku")
            .build();
    return productVariantRepository.save(variant);
  }

  private CustomerOrder createOrder(
      AppUser user,
      FulfillmentType fulfillment,
      Instant requestedAt,
      String notes,
      String shipPhone,
      Product product,
      ProductVariant variant) {
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user)
            .status(OrderStatus.CREATED)
            .fulfillment(fulfillment)
            .notes(notes)
            .requestedAt(requestedAt)
            .shipName("Envio")
            .shipPhone(shipPhone)
            .shipLine1("Calle 1")
            .shipCity("MVD")
            .shipReference("Ref")
            .subtotalAmount(new BigDecimal("100.00"))
            .taxAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(new BigDecimal("100.00"))
            .build();

    OrderItem item =
        OrderItem.builder()
            .order(order)
            .product(product)
            .productVariant(variant)
            .productName(product.getName())
            .variantName(variant.getName())
            .unitPrice(new BigDecimal("100.00"))
            .quantity(1)
            .lineTotal(new BigDecimal("100.00"))
            .build();
    order.addItem(item);

    return orderRepository.save(order);
  }
}
