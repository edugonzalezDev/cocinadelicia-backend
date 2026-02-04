package com.cocinadelicia.backend.user.service.impl;

import static com.cocinadelicia.backend.user.repository.spec.UserSpecifications.*;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.service.AdminUserService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

  private final UserRepository userRepository;
  private final CustomerOrderRepository customerOrderRepository;

  @Override
  public PageResponse<AdminUserListItemDTO> listUsers(AdminUserFilter filter, Pageable pageable) {
    log.info(
        "AdminUserService.listUsers filters=[q={}, roles={}, isActive={}, hasPendingOrders={}] page={} size={}",
        filter.q(),
        filter.roles(),
        filter.isActive(),
        filter.hasPendingOrders(),
        pageable.getPageNumber(),
        pageable.getPageSize());

    // 1. Construir Specification combinando filtros
    Specification<AppUser> spec =
        Specification.allOf(
            searchByKeyword(filter.q()),
            hasRolesIn(filter.roles()),
            isActive(filter.isActive()),
            hasPendingOrders(filter.hasPendingOrders()));

    // 2. Ejecutar query con paginación
    Page<AppUser> page = userRepository.findAll(spec, pageable);

    // 3. Calcular hasPendingOrders para usuarios de la página actual
    List<Long> userIds = page.getContent().stream().map(AppUser::getId).toList();
    Set<Long> usersWithPendingOrders = getUsersWithPendingOrders(userIds);

    // 4. Mapear a DTO
    List<AdminUserListItemDTO> content =
        page.getContent().stream().map(user -> mapToDTO(user, usersWithPendingOrders)).toList();

    log.debug("AdminUserService.listUsers returned {} users", content.size());

    return new PageResponse<>(
        content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  /**
   * Obtiene los IDs de usuarios que tienen pedidos pendientes (status != DELIVERED/CANCELLED).
   *
   * @param userIds lista de IDs de usuarios a verificar
   * @return conjunto de IDs con pedidos pendientes
   */
  private Set<Long> getUsersWithPendingOrders(List<Long> userIds) {
    if (userIds.isEmpty()) return Set.of();

    // Query optimizada: SELECT DISTINCT user_id FROM customer_order
    // WHERE user_id IN (:userIds) AND status NOT IN ('DELIVERED', 'CANCELLED')
    return customerOrderRepository
        .findAll(
            (root, query, cb) -> {
              query.distinct(true);
              return cb.and(
                  root.get("user").get("id").in(userIds),
                  cb.not(
                      root.get("status").in(OrderStatus.DELIVERED, OrderStatus.CANCELLED)));
            })
        .stream()
        .map(order -> order.getUser().getId())
        .collect(Collectors.toSet());
  }

  /**
   * Mapea entidad AppUser a DTO con roles y flag de pedidos pendientes.
   *
   * @param user entidad AppUser (con roles cargados)
   * @param usersWithPendingOrders set de IDs con pedidos pendientes
   * @return DTO para Admin
   */
  private AdminUserListItemDTO mapToDTO(AppUser user, Set<Long> usersWithPendingOrders) {
    Set<String> roleNames =
        user.getRoles().stream()
            .map(userRole -> userRole.getRole().getName().name())
            .collect(Collectors.toSet());

    boolean hasPending = usersWithPendingOrders.contains(user.getId());

    return new AdminUserListItemDTO(
        user.getId(),
        user.getCognitoUserId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone(),
        user.isActive(),
        roleNames,
        hasPending);
  }
}
