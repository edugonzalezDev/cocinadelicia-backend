package com.cocinadelicia.backend.order.chef.service.impl;

import com.cocinadelicia.backend.common.exception.ForbiddenException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.chef.dto.OrderChefResponse;
import com.cocinadelicia.backend.order.chef.mapper.OrderChefMapper;
import com.cocinadelicia.backend.order.chef.service.OrderChefService;
import com.cocinadelicia.backend.order.domain.OrderStatusTransition;
import com.cocinadelicia.backend.order.events.OrderWebSocketPublisher;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderChefServiceImpl implements OrderChefService {

  private final CustomerOrderRepository orderRepository;
  private final OrderStatusHistoryRepository statusHistoryRepository;
  private final CurrentUserService currentUserService;
  private final OrderWebSocketPublisher orderWebSocketPublisher;

  @Override
  public Page<OrderChefResponse> getAssignedOrders(Pageable pageable) {
    String chefEmail = currentUserService.getCurrentUserEmail();

    Specification<CustomerOrder> spec =
        (root, query, cb) ->
            cb.and(
                cb.equal(root.get("assignedChefEmail"), chefEmail),
                cb.isNull(root.get("deletedAt")));

    Page<CustomerOrder> orders = orderRepository.findAll(spec, pageable);

    return orders.map(OrderChefMapper::toChefResponse);
  }

  @Override
  public Page<OrderChefResponse> getActiveOrders(Pageable pageable) {
    String chefEmail = currentUserService.getCurrentUserEmail();

    List<OrderStatus> activeStatuses =
        List.of(OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY);

    Specification<CustomerOrder> spec =
        (root, query, cb) ->
            cb.and(
                cb.equal(root.get("assignedChefEmail"), chefEmail),
                root.get("status").in(activeStatuses),
                cb.isNull(root.get("deletedAt")));

    Page<CustomerOrder> orders = orderRepository.findAll(spec, pageable);

    return orders.map(OrderChefMapper::toChefResponse);
  }

  @Override
  public OrderChefResponse updateStatus(Long orderId, OrderStatus newStatus) {
    String chefEmail = currentUserService.getCurrentUserEmail();

    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    // Validar que la orden esté asignada al chef actual
    if (order.getAssignedChefEmail() == null
        || !order.getAssignedChefEmail().equals(chefEmail)) {
      throw new ForbiddenException("This order is not assigned to you");
    }

    OrderStatus oldStatus = order.getStatus();

    // Validar que el chef puede hacer esta transición
    OrderStatusTransition.validateChefTransition(oldStatus, newStatus);

    // Actualizar estado
    order.setStatus(newStatus);
    CustomerOrder saved = orderRepository.save(order);

    // Registrar en historial
    OrderStatusHistory historyEntry =
        OrderStatusHistory.builder()
            .orderId(orderId)
            .fromStatus(oldStatus)
            .toStatus(newStatus)
            .changedBy(chefEmail)
            .changedAt(Instant.now())
            .reason("Chef status update")
            .build();

    statusHistoryRepository.save(historyEntry);

    log.info(
        "ChefOrderStatusChanged orderId={} from={} to={} by={}",
        orderId,
        oldStatus,
        newStatus,
        chefEmail);

    // Retornar response
    OrderChefResponse response = OrderChefMapper.toChefResponse(saved);

    return response;
  }
}
