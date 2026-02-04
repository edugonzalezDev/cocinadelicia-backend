package com.cocinadelicia.backend.user.repository.spec;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.Role;
import com.cocinadelicia.backend.user.model.UserRole;
import jakarta.persistence.criteria.*;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

/** Specifications para búsqueda y filtrado de usuarios (Admin). */
public class UserSpecifications {

  /**
   * Búsqueda por keyword en email, firstName, lastName o phone (case-insensitive).
   *
   * @param q texto de búsqueda (si null o blank, no aplica filtro)
   * @return Specification con OR lógico sobre los campos mencionados
   */
  public static Specification<AppUser> searchByKeyword(String q) {
    if (q == null || q.isBlank()) return null;

    String pattern = "%" + q.trim().toLowerCase() + "%";

    return (root, query, cb) -> {
      Predicate emailLike = cb.like(cb.lower(root.get("email")), pattern);
      Predicate firstNameLike = cb.like(cb.lower(root.get("firstName")), pattern);
      Predicate lastNameLike = cb.like(cb.lower(root.get("lastName")), pattern);
      Predicate phoneLike = cb.like(cb.lower(root.get("phone")), pattern);

      return cb.or(emailLike, firstNameLike, lastNameLike, phoneLike);
    };
  }

  /**
   * Filtrar usuarios que tengan al menos uno de los roles especificados (OR lógico).
   *
   * @param roleNames conjunto de roles (si null o vacío, no aplica filtro)
   * @return Specification con JOIN sobre UserRole y Role
   */
  public static Specification<AppUser> hasRolesIn(Set<RoleName> roleNames) {
    if (roleNames == null || roleNames.isEmpty()) return null;

    return (root, query, cb) -> {
      // Para evitar duplicados en el resultado, usamos DISTINCT
      query.distinct(true);

      // JOIN: AppUser -> UserRole -> Role
      Join<AppUser, UserRole> userRoleJoin = root.join("roles", JoinType.INNER);
      Join<UserRole, Role> roleJoin = userRoleJoin.join("role", JoinType.INNER);

      // Filtrar por role.name IN (:roleNames)
      return roleJoin.get("name").in(roleNames);
    };
  }

  /**
   * Filtrar por estado activo/inactivo.
   *
   * @param isActive true/false (si null, no aplica filtro)
   * @return Specification simple sobre campo isActive
   */
  public static Specification<AppUser> isActive(Boolean isActive) {
    if (isActive == null) return null;
    return (root, query, cb) -> cb.equal(root.get("isActive"), isActive);
  }

  /**
   * Filtrar usuarios con/sin pedidos pendientes.
   *
   * <p>Pedido pendiente: status NOT IN ('DELIVERED', 'CANCELLED') y deleted_at IS NULL.
   *
   * @param hasPending true (tiene pendientes), false (no tiene), null (no aplica filtro)
   * @return Specification con EXISTS/NOT EXISTS subquery
   */
  public static Specification<AppUser> hasPendingOrders(Boolean hasPending) {
    if (hasPending == null) return null;

    return (root, query, cb) -> {
      // Subquery: SELECT 1 FROM customer_order WHERE user_id = user.id AND status NOT IN (...)
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<CustomerOrder> orderRoot = subquery.from(CustomerOrder.class);

      subquery.select(cb.literal(1L));

      Predicate userMatch = cb.equal(orderRoot.get("user").get("id"), root.get("id"));
      Predicate statusNotFinal =
          cb.not(orderRoot.get("status").in(OrderStatus.DELIVERED, OrderStatus.CANCELLED));

      subquery.where(cb.and(userMatch, statusNotFinal));

      // Si hasPending = true, EXISTS; si false, NOT EXISTS
      return hasPending ? cb.exists(subquery) : cb.not(cb.exists(subquery));
    };
  }
}
