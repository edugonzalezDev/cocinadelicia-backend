package com.cocinadelicia.backend.order.admin.controller;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.order.admin.dto.AssignChefRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderItemsRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderStatusAdminRequest;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderItemRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.CustomerAddress;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.repository.CustomerAddressRepository;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.repository.UserRoleRepository;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(
    properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.flyway.enabled=false"})
class OrderAdminControllerActionsTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CustomerOrderRepository orderRepository;
  @Autowired private OrderItemRepository orderItemRepository;
  @Autowired private OrderStatusHistoryRepository statusHistoryRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRoleRepository userRoleRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductVariantRepository productVariantRepository;
  @Autowired private CustomerAddressRepository customerAddressRepository;
  @Autowired private EntityManager entityManager;

  @MockitoBean private CurrentUserService currentUserService;

  @BeforeEach
  void setUp() {
    Mockito.when(currentUserService.getCurrentUserEmail()).thenReturn("admin@test.com");
    statusHistoryRepository.deleteAll();
    orderItemRepository.deleteAll();
    orderRepository.deleteAll();
    productVariantRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    userRoleRepository.deleteAll();
    roleRepository.deleteAll();
    customerAddressRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void patchStatus_ok_createsHistory() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);

    var request = new UpdateOrderStatusAdminRequest(OrderStatus.CONFIRMED, "ok");

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/status", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONFIRMED"));

    List<OrderStatusHistory> history =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(order.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, history.size());
    org.junit.jupiter.api.Assertions.assertEquals(
        OrderStatus.CREATED, history.get(0).getFromStatus());
    org.junit.jupiter.api.Assertions.assertEquals(
        OrderStatus.CONFIRMED, history.get(0).getToStatus());
  }

  @Test
  void patchStatus_invalidTransition_returns400() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);

    var request = new UpdateOrderStatusAdminRequest(OrderStatus.DELIVERED, "bad");

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/status", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
  }

  @Test
  void patchStatus_notFound_returns404() throws Exception {
    var request = new UpdateOrderStatusAdminRequest(OrderStatus.CONFIRMED, "ok");

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/status", 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void assignChef_ok_updatesAssignedChef() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);
    AppUser chef = createUser("chef@test.com");
    Role chefRole = roleRepository.save(Role.builder().name(RoleName.CHEF).build());
    userRoleRepository.save(new UserRole(chef, chefRole));

    var request = new AssignChefRequest("chef@test.com");

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/assign-chef", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.assignedChefEmail").value("chef@test.com"));
  }

  @Test
  void assignChef_notChef_returns400() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);
    createUser("chef@test.com");
    roleRepository.save(Role.builder().name(RoleName.CHEF).build());

    var request = new AssignChefRequest("chef@test.com");

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/assign-chef", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("USER_NOT_CHEF"));
  }

  @Test
  void patchItems_updatesQuantitiesAndRemovesItem() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);
    OrderItem firstItem = order.getItems().get(0);
    OrderItem secondItem = order.getItems().get(1);

    var request =
        new UpdateOrderItemsRequest(
            List.of(
                new UpdateOrderItemsRequest.OrderItemUpdate(
                    firstItem.getId(), null, null, 2, List.of()),
                new UpdateOrderItemsRequest.OrderItemUpdate(
                    secondItem.getId(), null, null, 0, List.of())));

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/items", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.totalAmount").value(200.00));
  }

  @Test
  void patchItems_addsNewItem() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);
    ProductVariant newVariant = createProductWithVariant("Empanada");

    var request =
        new UpdateOrderItemsRequest(
            List.of(
                new UpdateOrderItemsRequest.OrderItemUpdate(
                    0L, newVariant.getProduct().getId(), newVariant.getId(), 1, List.of())));

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/items", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(3))
        .andExpect(jsonPath("$.totalAmount").value(300.00));
  }

  @Test
  void patchItems_allRemoved_returns400() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);
    OrderItem firstItem = order.getItems().get(0);
    OrderItem secondItem = order.getItems().get(1);

    var request =
        new UpdateOrderItemsRequest(
            List.of(
                new UpdateOrderItemsRequest.OrderItemUpdate(
                    firstItem.getId(), null, null, 0, List.of()),
                new UpdateOrderItemsRequest.OrderItemUpdate(
                    secondItem.getId(), null, null, 0, List.of())));

    mockMvc
        .perform(
            patch("/api/admin/orders/{id}/items", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("ORDER_ITEMS_EMPTY"));
  }

  @Test
  void getDetails_returnsOrderDetails() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);

    mockMvc
        .perform(get("/api/admin/orders/{id}/details", order.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(order.getId()))
        .andExpect(jsonPath("$.items.length()").value(2));
  }

  @Test
  void getDetails_notFound_returns404() throws Exception {
    mockMvc.perform(get("/api/admin/orders/{id}/details", 9999L)).andExpect(status().isNotFound());
  }

  @Test
  void getCustomer_returnsCustomerData() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerAddress address =
        customerAddressRepository.save(
            CustomerAddress.builder()
                .user(user)
                .label("Casa")
                .line1("Calle 123")
                .city("MVD")
                .region("MVD")
                .postalCode("11000")
                .reference("Puerta azul")
                .build());
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);

    mockMvc
        .perform(get("/api/admin/orders/{id}/customer", order.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(user.getId()))
        .andExpect(jsonPath("$.addresses.length()").value(1))
        .andExpect(jsonPath("$.addresses[0].id").value(address.getId()));
  }

  @Test
  void deleteOrder_softDeletes() throws Exception {
    AppUser user = createUser("customer@test.com");
    CustomerOrder order = createOrder(user, OrderStatus.CREATED);

    mockMvc
        .perform(delete("/api/admin/orders/{id}", order.getId()))
        .andExpect(status().isNoContent());

    Object[] row =
        (Object[])
            entityManager
                .createNativeQuery(
                    "SELECT status, deleted_at, deleted_by FROM customer_order WHERE id = ?")
                .setParameter(1, order.getId())
                .getSingleResult();

    org.junit.jupiter.api.Assertions.assertEquals("CANCELLED", row[0]);
    org.junit.jupiter.api.Assertions.assertNotNull(row[1]);
    org.junit.jupiter.api.Assertions.assertEquals("admin@test.com", row[2]);
  }

  private AppUser createUser(String email) {
    // Reutiliza si ya existe para evitar colisiones de unicidad entre tests
    return userRepository
        .findByEmail(email)
        .orElseGet(
            () ->
                userRepository.save(
                    AppUser.builder()
                        .cognitoUserId("cognito-" + email)
                        .firstName("Test")
                        .lastName("User")
                        .email(email)
                        .phone("099000")
                        .isActive(true)
                        .build()));
  }

  private CustomerOrder createOrder(AppUser user, OrderStatus status) {
    ProductVariant variant = createProductWithVariant("Pizza");
    ProductVariant variant2 = createProductVariant(variant.getProduct(), "Pizza XL");

    CustomerOrder order =
        CustomerOrder.builder()
            .user(user)
            .status(status)
            .fulfillment(FulfillmentType.DELIVERY)
            .notes("nota")
            .requestedAt(Instant.now())
            .shipName("Envio")
            .shipPhone("099123")
            .shipLine1("Calle 1")
            .shipCity("MVD")
            .shipReference("Ref")
            .subtotalAmount(new BigDecimal("200.00"))
            .taxAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(new BigDecimal("200.00"))
            .build();

    OrderItem item =
        OrderItem.builder()
            .order(order)
            .product(variant.getProduct())
            .productVariant(variant)
            .productName(variant.getProduct().getName())
            .variantName(variant.getName())
            .unitPrice(new BigDecimal("100.00"))
            .quantity(1)
            .lineTotal(new BigDecimal("100.00"))
            .build();
    OrderItem item2 =
        OrderItem.builder()
            .order(order)
            .product(variant2.getProduct())
            .productVariant(variant2)
            .productName(variant2.getProduct().getName())
            .variantName(variant2.getName())
            .unitPrice(new BigDecimal("100.00"))
            .quantity(1)
            .lineTotal(new BigDecimal("100.00"))
            .build();

    order.addItem(item);
    order.addItem(item2);

    return orderRepository.save(order);
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
}
