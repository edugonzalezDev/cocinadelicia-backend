package com.cocinadelicia.backend.order.admin.service.impl;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.admin.dto.CreateOrderAdminRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderFilterRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderStatsResponse;
import com.cocinadelicia.backend.order.admin.mapper.OrderAdminMapper;
import com.cocinadelicia.backend.order.admin.service.OrderAdminService;
import com.cocinadelicia.backend.order.domain.OrderStatusTransition;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.events.OrderWebSocketPublisher;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
public class OrderAdminServiceImpl implements OrderAdminService {

  private final CustomerOrderRepository orderRepository;
  private final OrderStatusHistoryRepository statusHistoryRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;
  private final PriceQueryPort priceQueryPort;
  private final CurrentUserService currentUserService;
  private final OrderWebSocketPublisher orderWebSocketPublisher;

  private static final BigDecimal ZERO = new BigDecimal("0.00");

  @Override
  public OrderAdminResponse createOrderForUser(CreateOrderAdminRequest request) {
    // 1. Validaciones
    if (request.items() == null || request.items().isEmpty()) {
      throw new BadRequestException("ORDER_ITEMS_EMPTY", "Debe agregar al menos un ítem.");
    }

    for (OrderItemRequest it : request.items()) {
      if (it.quantity() <= 0) {
        throw new BadRequestException(
            "INVALID_QUANTITY", "La cantidad de cada ítem debe ser >= 1.");
      }
    }

    if (request.fulfillment() == FulfillmentType.DELIVERY) {
      var s = request.shipping();
      if (s == null
          || isBlank(s.name())
          || isBlank(s.phone())
          || isBlank(s.line1())
          || isBlank(s.city())) {
        throw new BadRequestException(
            "DELIVERY_ADDRESS_REQUIRED",
            "Complete los datos de envío (nombre, teléfono, dirección y ciudad).");
      }
    }

    // 2. Buscar o crear usuario por email
    AppUser user =
        userRepository
            .findByEmail(request.userEmail())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "USER_NOT_FOUND",
                        "Usuario no encontrado con email: " + request.userEmail()));

    // 3. Crear orden base
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user)
            .status(OrderStatus.CREATED)
            .fulfillment(request.fulfillment())
            .notes(request.notes())
            .requestedAt(request.requestedAt())
            .assignedChefEmail(request.assignedChefEmail())
            .build();

    // 4. Snapshot shipping si DELIVERY
    if (request.fulfillment() == FulfillmentType.DELIVERY) {
      var s = request.shipping();
      order.setShipName(s.name());
      order.setShipPhone(s.phone());
      order.setShipLine1(s.line1());
      order.setShipLine2(s.line2());
      order.setShipCity(s.city());
      order.setShipRegion(s.region());
      order.setShipPostalCode(s.postalCode());
      order.setShipReference(s.reference());
    }

    // 5. Procesar items y calcular totales
    BigDecimal subtotal = ZERO;

    for (OrderItemRequest it : request.items()) {
      Product product =
          productRepository
              .findById(it.productId())
              .orElseThrow(
                  () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado."));

      ProductVariant variant =
          productVariantRepository
              .findById(it.productVariantId())
              .orElseThrow(
                  () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));

      if (variant.getProduct() == null || !variant.getProduct().getId().equals(product.getId())) {
        throw new BadRequestException(
            "VARIANT_MISMATCH", "La variante no pertenece al producto indicado.");
      }

      BigDecimal unitPrice =
          priceQueryPort
              .findActivePriceByVariantId(variant.getId())
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "PRICE_NOT_FOUND",
                          "No hay precio vigente para la variante seleccionada."));

      unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
      BigDecimal lineTotal =
          unitPrice.multiply(BigDecimal.valueOf(it.quantity())).setScale(2, RoundingMode.HALF_UP);
      subtotal = subtotal.add(lineTotal);

      OrderItem item =
          OrderItem.builder()
              .order(order)
              .product(product)
              .productVariant(variant)
              .productName(product.getName())
              .variantName(variant.getName())
              .unitPrice(unitPrice)
              .quantity(it.quantity())
              .lineTotal(lineTotal)
              .build();

      order.addItem(item);
    }

    // 6. Totales
    order.setSubtotalAmount(subtotal);
    order.setTaxAmount(ZERO);
    order.setDiscountAmount(ZERO);
    order.setTotalAmount(subtotal);

    // 7. Persistir
    CustomerOrder saved = orderRepository.save(order);

    // 8. Registrar en historial
    String adminEmail = currentUserService.getCurrentUserEmail();
    OrderStatusHistory historyEntry =
        OrderStatusHistory.builder()
            .orderId(saved.getId())
            .fromStatus(null)
            .toStatus(OrderStatus.CREATED)
            .changedBy(adminEmail)
            .changedAt(Instant.now())
            .reason("Orden creada por administrador")
            .build();
    statusHistoryRepository.save(historyEntry);

    log.info(
        "OrderCreatedByAdmin orderId={} userEmail={} adminEmail={} items={} total={}",
        saved.getId(),
        request.userEmail(),
        adminEmail,
        saved.getItems().size(),
        saved.getTotalAmount());

    // 9. Retornar response
    List<OrderStatusHistory> history =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(saved.getId());
    return OrderAdminMapper.toAdminResponse(saved, history);
  }

  @Override
  public Page<OrderAdminResponse> getAllOrders(OrderFilterRequest filters, Pageable pageable) {
    Specification<CustomerOrder> spec = buildSpecification(filters);

    Page<CustomerOrder> ordersPage = orderRepository.findAll(spec, pageable);

    return ordersPage.map(
        order -> {
          List<OrderStatusHistory> history =
              statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(order.getId());
          return OrderAdminMapper.toAdminResponse(order, history);
        });
  }

  @Override
  public OrderAdminResponse getOrderById(Long id) {
    CustomerOrder order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

    List<OrderStatusHistory> history =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(id);

    return OrderAdminMapper.toAdminResponse(order, history);
  }

  @Override
  public OrderAdminResponse updateStatus(Long orderId, OrderStatus newStatus, String reason) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    OrderStatus oldStatus = order.getStatus();

    // Validar transición
    OrderStatusTransition.validateTransition(oldStatus, newStatus);

    // Actualizar estado
    order.setStatus(newStatus);

    // Si se marca como entregada, registrar timestamp
    if (newStatus == OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
      order.setDeliveredAt(Instant.now());
    }

    CustomerOrder saved = orderRepository.save(order);

    // Registrar en historial
    String changedBy = currentUserService.getCurrentUserEmail();
    OrderStatusHistory historyEntry =
        OrderStatusHistory.builder()
            .orderId(orderId)
            .fromStatus(oldStatus)
            .toStatus(newStatus)
            .changedBy(changedBy)
            .changedAt(Instant.now())
            .reason(reason)
            .build();

    statusHistoryRepository.save(historyEntry);

    log.info(
        "OrderStatusChanged orderId={} from={} to={} by={} reason={}",
        orderId,
        oldStatus,
        newStatus,
        changedBy,
        reason);

    // Publicar evento WebSocket (convertir a OrderResponse para compatibilidad)
    List<OrderStatusHistory> fullHistory =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId);
    OrderAdminResponse response = OrderAdminMapper.toAdminResponse(saved, fullHistory);

    // No publicamos admin response por WebSocket, usamos la versión estándar
    // orderWebSocketPublisher.publishOrderUpdated(response);

    return response;
  }

  @Override
  public OrderAdminResponse assignChef(Long orderId, String chefEmail) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    order.setAssignedChefEmail(chefEmail);
    CustomerOrder saved = orderRepository.save(order);

    String adminEmail = currentUserService.getCurrentUserEmail();
    log.info(
        "ChefAssigned orderId={} chefEmail={} by={}", orderId, chefEmail, adminEmail);

    List<OrderStatusHistory> history =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId);

    return OrderAdminMapper.toAdminResponse(saved, history);
  }

  @Override
  public void deleteOrder(Long orderId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    String deletedBy = currentUserService.getCurrentUserEmail();
    order.softDelete(deletedBy);
    orderRepository.save(order);

    log.info("OrderDeleted orderId={} by={}", orderId, deletedBy);
  }

  @Override
  public OrderStatsResponse getStats() {
    LocalDate today = LocalDate.now(ZoneId.of("America/Montevideo"));
    Instant startOfDay = today.atStartOfDay(ZoneId.of("America/Montevideo")).toInstant();
    Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.of("America/Montevideo")).toInstant();

    // Conteo por estado (solo no eliminadas)
    Specification<CustomerOrder> notDeleted =
        (root, query, cb) -> cb.isNull(root.get("deletedAt"));

    long totalActive = orderRepository.count(notDeleted);

    Specification<CustomerOrder> created =
        notDeleted.and((root, query, cb) -> cb.equal(root.get("status"), OrderStatus.CREATED));
    long createdCount = orderRepository.count(created);

    Specification<CustomerOrder> confirmed =
        notDeleted.and((root, query, cb) -> cb.equal(root.get("status"), OrderStatus.CONFIRMED));
    long confirmedCount = orderRepository.count(confirmed);

    Specification<CustomerOrder> preparing =
        notDeleted.and((root, query, cb) -> cb.equal(root.get("status"), OrderStatus.PREPARING));
    long preparingCount = orderRepository.count(preparing);

    Specification<CustomerOrder> ready =
        notDeleted.and((root, query, cb) -> cb.equal(root.get("status"), OrderStatus.READY));
    long readyCount = orderRepository.count(ready);

    Specification<CustomerOrder> outForDelivery =
        notDeleted.and(
            (root, query, cb) -> cb.equal(root.get("status"), OrderStatus.OUT_FOR_DELIVERY));
    long outForDeliveryCount = orderRepository.count(outForDelivery);

    // Entregadas hoy
    Specification<CustomerOrder> deliveredToday =
        (root, query, cb) ->
            cb.and(
                cb.equal(root.get("status"), OrderStatus.DELIVERED),
                cb.between(root.get("deliveredAt"), startOfDay, endOfDay));
    long deliveredTodayCount = orderRepository.count(deliveredToday);

    // Canceladas hoy
    Specification<CustomerOrder> CANCELLEDToday =
        (root, query, cb) ->
            cb.and(
                cb.equal(root.get("status"), OrderStatus.CANCELLED),
                cb.between(root.get("updatedAt"), startOfDay, endOfDay));
    long CANCELLEDTodayCount = orderRepository.count(CANCELLEDToday);

    // Ingresos del día (suma de totalAmount de órdenes entregadas hoy)
    List<CustomerOrder> deliveredOrders = orderRepository.findAll(deliveredToday);
    BigDecimal revenueToday =
        deliveredOrders.stream()
            .map(CustomerOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Ticket promedio
    BigDecimal avgTicket = BigDecimal.ZERO;
    if (deliveredTodayCount > 0) {
      avgTicket =
          revenueToday.divide(
              BigDecimal.valueOf(deliveredTodayCount), 2, java.math.RoundingMode.HALF_UP);
    }

    return new OrderStatsResponse(
        totalActive,
        createdCount,
        confirmedCount,
        preparingCount,
        readyCount,
        outForDeliveryCount,
        deliveredTodayCount,
        CANCELLEDTodayCount,
        revenueToday,
        avgTicket);
  }

  private Specification<CustomerOrder> buildSpecification(OrderFilterRequest filters) {
    List<Specification<CustomerOrder>> specs = new ArrayList<>();

    if (filters.getStatus() != null) {
      specs.add((root, query, cb) -> cb.equal(root.get("status"), filters.getStatus()));
    }

    if (filters.getUserEmail() != null && !filters.getUserEmail().isBlank()) {
      specs.add(
          (root, query, cb) ->
              cb.like(
                  cb.lower(root.get("user").get("email")),
                  "%" + filters.getUserEmail().toLowerCase() + "%"));
    }

    if (filters.getAssignedChefEmail() != null && !filters.getAssignedChefEmail().isBlank()) {
      specs.add(
          (root, query, cb) ->
              cb.like(
                  cb.lower(root.get("assignedChefEmail")),
                  "%" + filters.getAssignedChefEmail().toLowerCase() + "%"));
    }

    if (filters.getCreatedAfter() != null) {
      Instant start =
          filters
              .getCreatedAfter()
              .atStartOfDay(ZoneId.of("America/Montevideo"))
              .toInstant();
      specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
    }

    if (filters.getCreatedBefore() != null) {
      Instant end =
          filters
              .getCreatedBefore()
              .plusDays(1)
              .atStartOfDay(ZoneId.of("America/Montevideo"))
              .toInstant();
      specs.add((root, query, cb) -> cb.lessThan(root.get("createdAt"), end));
    }

    if (filters.getMinAmount() != null) {
      specs.add(
          (root, query, cb) ->
              cb.greaterThanOrEqualTo(root.get("totalAmount"), filters.getMinAmount()));
    }

    if (filters.getMaxAmount() != null) {
      specs.add(
          (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalAmount"), filters.getMaxAmount()));
    }

    // Por defecto NO mostrar eliminadas
    if (filters.getIncludeDeleted() == null || !filters.getIncludeDeleted()) {
      specs.add((root, query, cb) -> cb.isNull(root.get("deletedAt")));
    }

    // Combinar todas las especificaciones con AND
    if (specs.isEmpty()) {
      return null;
    }
    return specs.stream().reduce(Specification::and).orElse(null);
  }

  private boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
