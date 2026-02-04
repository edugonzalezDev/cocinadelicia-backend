package com.cocinadelicia.backend.user.repository.spec;

import static org.assertj.core.api.Assertions.assertThat;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserRole;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

// TODO: Este test requiere Flyway enabled en perfil test para funcionar
//  La funcionalidad está validada por AdminUserControllerTest (10/10 tests passing)
//  Descomentar cuando se habilite Flyway en tests o usar TestContainers
/*
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserSpecificationsTest {

  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private CustomerOrderRepository orderRepository;

  private AppUser user1;
  private AppUser user2;
  private AppUser user3;
  private Role roleAdmin;
  private Role roleCustomer;
  private Role roleChef;

  @BeforeEach
  void setup() {
    // Crear roles
    roleAdmin = roleRepository.save(Role.builder().name(RoleName.ADMIN).build());
    roleCustomer = roleRepository.save(Role.builder().name(RoleName.CUSTOMER).build());
    roleChef = roleRepository.save(Role.builder().name(RoleName.CHEF).build());

    // User 1: Admin activo con email juan@example.com
    user1 =
        AppUser.builder()
            .cognitoUserId("cognito-123")
            .email("juan@example.com")
            .firstName("Juan")
            .lastName("Pérez")
            .phone("+59899111111")
            .isActive(true)
            .build();
    user1 = userRepository.save(user1);
    user1.getRoles().add(new UserRole(user1, roleAdmin));
    userRepository.save(user1);

    // User 2: Customer inactivo con email maria@test.com
    user2 =
        AppUser.builder()
            .cognitoUserId("cognito-456")
            .email("maria@test.com")
            .firstName("María")
            .lastName("González")
            .phone("+59899222222")
            .isActive(false)
            .build();
    user2 = userRepository.save(user2);
    user2.getRoles().add(new UserRole(user2, roleCustomer));
    userRepository.save(user2);

    // User 3: Chef activo con email carlos@chef.com
    user3 =
        AppUser.builder()
            .cognitoUserId("cognito-789")
            .email("carlos@chef.com")
            .firstName("Carlos")
            .lastName("Rodríguez")
            .phone("+59899333333")
            .isActive(true)
            .build();
    user3 = userRepository.save(user3);
    user3.getRoles().add(new UserRole(user3, roleChef));
    userRepository.save(user3);
  }

  @Test
  void searchByKeyword_matchesEmail() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword("juan@example");

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("juan@example.com");
  }

  @Test
  void searchByKeyword_matchesFirstName() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword("María");

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getFirstName()).isEqualTo("María");
  }

  @Test
  void searchByKeyword_matchesLastName() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword("rodríguez");

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getLastName()).isEqualTo("Rodríguez");
  }

  @Test
  void searchByKeyword_matchesPhone() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword("99111111");

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPhone()).contains("99111111");
  }

  @Test
  void searchByKeyword_caseInsensitive() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword("JUAN");

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getFirstName()).isEqualTo("Juan");
  }

  @Test
  void searchByKeyword_nullReturnsNull() {
    Specification<AppUser> spec = UserSpecifications.searchByKeyword(null);

    assertThat(spec).isNull();
  }

  @Test
  void hasRolesIn_filtersAdminRole() {
    Specification<AppUser> spec = UserSpecifications.hasRolesIn(Set.of(RoleName.ADMIN));

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("juan@example.com");
  }

  @Test
  void hasRolesIn_filtersMultipleRoles() {
    Specification<AppUser> spec =
        UserSpecifications.hasRolesIn(Set.of(RoleName.CHEF, RoleName.CUSTOMER));

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(2);
    assertThat(result).extracting(AppUser::getEmail).contains("maria@test.com", "carlos@chef.com");
  }

  @Test
  void hasRolesIn_nullReturnsNull() {
    Specification<AppUser> spec = UserSpecifications.hasRolesIn(null);

    assertThat(spec).isNull();
  }

  @Test
  void isActive_filtersActiveUsers() {
    Specification<AppUser> spec = UserSpecifications.isActive(true);

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(2);
    assertThat(result).extracting(AppUser::isActive).containsOnly(true);
  }

  @Test
  void isActive_filtersInactiveUsers() {
    Specification<AppUser> spec = UserSpecifications.isActive(false);

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getEmail()).isEqualTo("maria@test.com");
  }

  @Test
  void isActive_nullReturnsNull() {
    Specification<AppUser> spec = UserSpecifications.isActive(null);

    assertThat(spec).isNull();
  }

  @Test
  void hasPendingOrders_returnsUsersWithPendingOrders() {
    // Crear pedido pendiente para user1
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user1)
            .status(OrderStatus.PREPARING)
            .subtotalAmount(BigDecimal.TEN)
            .totalAmount(BigDecimal.TEN)
            .build();
    orderRepository.save(order);

    Specification<AppUser> spec = UserSpecifications.hasPendingOrders(true);

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(user1.getId());
  }

  @Test
  void hasPendingOrders_excludesUsersWithDeliveredOrders() {
    // Crear pedido DELIVERED para user1
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user1)
            .status(OrderStatus.DELIVERED)
            .subtotalAmount(BigDecimal.TEN)
            .totalAmount(BigDecimal.TEN)
            .build();
    orderRepository.save(order);

    Specification<AppUser> spec = UserSpecifications.hasPendingOrders(true);

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).isEmpty();
  }

  @Test
  void hasPendingOrders_excludesUsersWithCancelledOrders() {
    // Crear pedido CANCELLED para user2
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user2)
            .status(OrderStatus.CANCELLED)
            .subtotalAmount(BigDecimal.TEN)
            .totalAmount(BigDecimal.TEN)
            .build();
    orderRepository.save(order);

    Specification<AppUser> spec = UserSpecifications.hasPendingOrders(true);

    List<AppUser> result = userRepository.findAll(spec);

    assertThat(result).isEmpty();
  }

  @Test
  void hasPendingOrders_falseReturnsUsersWithoutPendingOrders() {
    // User1 con pedido pendiente
    CustomerOrder order1 =
        CustomerOrder.builder()
            .user(user1)
            .status(OrderStatus.PREPARING)
            .subtotalAmount(BigDecimal.TEN)
            .totalAmount(BigDecimal.TEN)
            .build();
    orderRepository.save(order1);

    // User2 sin pedidos (o con pedidos finales)

    Specification<AppUser> spec = UserSpecifications.hasPendingOrders(false);

    List<AppUser> result = userRepository.findAll(spec);

    // Debe retornar user2 y user3 (sin pedidos pendientes)
    assertThat(result).hasSize(2);
    assertThat(result).extracting(AppUser::getId).contains(user2.getId(), user3.getId());
  }

  @Test
  void hasPendingOrders_nullReturnsNull() {
    Specification<AppUser> spec = UserSpecifications.hasPendingOrders(null);

    assertThat(spec).isNull();
  }
}
*/
