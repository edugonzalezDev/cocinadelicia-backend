package com.cocinadelicia.backend.order.service.impl;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.domain.OrderStatusTransitionValidator;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderFilter;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.dto.UpdateOrderStatusRequest;
import com.cocinadelicia.backend.order.events.OrderWebSocketPublisher;
import com.cocinadelicia.backend.order.mapper.OrderMapper;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderItemRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.order.repository.spec.OrderSpecifications;
import com.cocinadelicia.backend.order.service.OrderService;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
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
public class OrderServiceImpl implements OrderService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;

  private final CustomerOrderRepository customerOrderRepository;
  private final OrderItemRepository orderItemRepository; // no se usa explícitamente
  private final OrderStatusHistoryRepository statusHistoryRepository;
  private final PriceQueryPort priceQueryPort;

  private final OrderWebSocketPublisher orderWebSocketPublisher;

  private static final BigDecimal ZERO = new BigDecimal("0.00");

  @Override
  public OrderResponse createOrder(CreateOrderRequest request, Long appUserId) {
    // 1️⃣ Validaciones mínimas del payload (con códigos)
    if (request.items() == null || request.items().isEmpty()) {
      throw new BadRequestException("ORDER_ITEMS_EMPTY", "Debe agregar al menos un ítem.");
    }

    for (OrderItemRequest it : request.items()) {
      if (it.quantity() <= 0) {
        throw new BadRequestException(
            "INVALID_QUANTITY", "La cantidad de cada ítem debe ser >= 1.");
      }
    }

    if (request.fulfillment() == null) {
      throw new BadRequestException(
          "FULFILLMENT_REQUIRED", "El tipo de fulfillment es obligatorio.");
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

    // 2️⃣ Usuario (dueño del pedido)
    AppUser user =
        userRepository
            .findById(appUserId)
            .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

    // 3️⃣ Crear entidad base del pedido
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user)
            .status(OrderStatus.CREATED)
            .fulfillment(request.fulfillment())
            .notes(request.notes())
            .requestedAt(request.requestedAt()) // ⬅️ NUEVO
            .build();

    // 4️⃣ Snapshot shipping si DELIVERY
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

    // 5️⃣ Armar items: validar producto/variante, obtener precio vigente y calcular totales
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

      // Validar que la variante pertenezca al producto
      if (variant.getProduct() == null || !variant.getProduct().getId().equals(product.getId())) {
        throw new BadRequestException(
            "VARIANT_MISMATCH", "La variante no pertenece al producto indicado.");
      }

      // Precio vigente
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

    // 6️⃣ Totales (por ahora tax=0 y discount=0)
    order.setSubtotalAmount(subtotal);
    order.setTaxAmount(ZERO);
    order.setDiscountAmount(ZERO);
    order.setTotalAmount(subtotal);

    // 7️⃣ Persistir (Cascade guarda items)
    CustomerOrder saved = customerOrderRepository.save(order);

    // 8️⃣ Armar respuesta DTO (canónica)
    OrderResponse response = OrderMapper.toResponse(saved);

    // 9️⃣ Logging de auditoría (INFO)
    log.info(
        "OrderCreated orderId={} userId={} userEmail={} fulfillment={} items={} total={}",
        saved.getId(),
        user.getId(),
        user.getEmail(),
        saved.getFulfillment(),
        saved.getItems() != null ? saved.getItems().size() : 0,
        saved.getTotalAmount());

    // 1️⃣0️⃣ Publicar evento en tiempo real
    orderWebSocketPublisher.publishOrderUpdated(response);

    // 1️⃣1️⃣ Devolver respuesta DTO
    return response;
  }

  // ==== Métodos de lectura ====

  @Override
  public OrderResponse getOrderById(Long orderId, Long appUserId) {
    CustomerOrder o =
        customerOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND", "Pedido no encontrado."));

    if (!o.getUser().getId().equals(appUserId)) {
      throw new NotFoundException(
          "ORDER_NOT_FOUND", "Pedido no pertenece al usuario."); // 404 para ocultar
    }
    return OrderMapper.toResponse(o);
  }

  @Override
  public Page<OrderResponse> getMyOrders(Long appUserId, Pageable pageable) {
    return customerOrderRepository.findByUser_Id(appUserId, pageable).map(OrderMapper::toResponse);
  }

  @Override
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    return customerOrderRepository.findAll(pageable).map(OrderMapper::toResponse);
  }

  @Override
  public OrderResponse updateStatus(
      Long orderId, String performedBy, UpdateOrderStatusRequest request) {

    if (request == null || request.status() == null) {
      throw new BadRequestException("STATUS_REQUIRED", "El estado es obligatorio.");
    }

    CustomerOrder order =
        customerOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND", "Pedido no encontrado."));

    OrderStatus current = order.getStatus();
    OrderStatus next = request.status();

    // ✅ Validar transición con logging de WARN si falla
    try {
      OrderStatusTransitionValidator.validateOrThrow(current, next);
    } catch (BadRequestException ex) {
      log.warn(
          "InvalidOrderStatusTransition orderId={} from={} to={} by={} reason={} code={}",
          order.getId(),
          current,
          next,
          performedBy,
          ex.getMessage(),
          ex.code());
      throw ex;
    }

    // ✅ Transición válida → aplicar cambio de estado
    order.setStatus(next);

    if (next == OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
      order.setDeliveredAt(java.time.Instant.now());
    }

    CustomerOrder saved = customerOrderRepository.save(order);

    OrderResponse response = OrderMapper.toResponse(saved);

    log.info(
        "OrderStatusChanged orderId={} oldStatus={} newStatus={} by={} note={}",
        saved.getId(),
        current,
        next,
        performedBy,
        request.note());

    orderWebSocketPublisher.publishOrderUpdated(response);

    return response;
  }

  @Override
  public Page<OrderResponse> findOrders(OrderFilter filter, Pageable pageable) {
    Specification<CustomerOrder> spec = Specification.allOf();

    if (filter != null) {
      if (filter.statuses() != null && !filter.statuses().isEmpty()) {
        spec = spec.and(OrderSpecifications.statusIn(filter.statuses()));
      }
      // Fechas YYYY-MM-DD inclusivas → [from 00:00, to+1d 00:00) en zona app
      java.time.ZoneId APP_ZONE = java.time.ZoneId.of("America/Montevideo");
      java.time.Instant fromInstant = null;
      java.time.Instant toExclusive = null;

      if (filter.from() != null) {
        fromInstant = filter.from().atStartOfDay(APP_ZONE).toInstant();
        spec = spec.and(OrderSpecifications.createdAtGte(fromInstant));
      }
      if (filter.to() != null) {
        toExclusive = filter.to().plusDays(1).atStartOfDay(APP_ZONE).toInstant();
        spec = spec.and(OrderSpecifications.createdAtLt(toExclusive));
      }
      if (filter.userId() != null) {
        spec = spec.and(OrderSpecifications.userIdEq(filter.userId()));
      }
    }
    // fallback de orden si no viene en pageable
    Pageable effective = pageable;
    if (effective.getSort().isUnsorted()) {
      effective =
          org.springframework.data.domain.PageRequest.of(
              pageable.getPageNumber(),
              pageable.getPageSize(),
              org.springframework.data.domain.Sort.by("createdAt").descending());
    }

    return customerOrderRepository.findAll(spec, effective).map(OrderMapper::toResponse);
  }

  @Override
  public Optional<OrderResponse> getCurrentOrder(Long appUserId) {
    // Buscar órdenes activas (no DELIVERED ni CANCELLED)
    List<OrderStatus> activeStatuses =
        List.of(
            OrderStatus.CREATED,
            OrderStatus.CONFIRMED,
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.OUT_FOR_DELIVERY);

    Specification<CustomerOrder> spec =
        OrderSpecifications.userIdEq(appUserId)
            .and(OrderSpecifications.statusIn(activeStatuses))
            .and((root, query, cb) -> cb.isNull(root.get("deletedAt")));

    org.springframework.data.domain.Pageable pageable =
        org.springframework.data.domain.PageRequest.of(
            0, 1, org.springframework.data.domain.Sort.by("createdAt").descending());

    Page<CustomerOrder> page = customerOrderRepository.findAll(spec, pageable);

    return page.hasContent()
        ? Optional.of(OrderMapper.toResponse(page.getContent().get(0)))
        : Optional.empty();
  }

  @Override
  public OrderResponse cancelOrder(Long orderId, String reason, Long appUserId) {
    CustomerOrder order =
        customerOrderRepository
            .findByIdAndUser_Id(orderId, appUserId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "ORDER_NOT_FOUND", "Pedido no encontrado o no pertenece al usuario."));

    if (order.isDeleted()) {
      throw new NotFoundException("ORDER_NOT_FOUND", "Pedido no encontrado.");
    }

    OrderStatus current = order.getStatus();

    // Validar que el cliente puede cancelar
    if (!com.cocinadelicia.backend.order.domain.OrderStatusTransition.canClientCancel(current)) {
      throw new BadRequestException(
          "CANNOT_CANCEL_ORDER",
          String.format(
              "No se puede cancelar un pedido en estado %s. Solo se pueden cancelar pedidos CREATED o CONFIRMED.",
              current));
    }

    // Cambiar estado a CANCELLED
    order.setStatus(OrderStatus.CANCELLED);
    CustomerOrder saved = customerOrderRepository.save(order);

    // Registrar en historial
    OrderStatusHistory historyEntry =
        OrderStatusHistory.builder()
            .orderId(orderId)
            .fromStatus(current)
            .toStatus(OrderStatus.CANCELLED)
            .changedBy(order.getUser().getEmail())
            .changedAt(java.time.Instant.now())
            .reason(reason != null ? reason : "Cliente canceló el pedido")
            .build();
    statusHistoryRepository.save(historyEntry);

    OrderResponse response = OrderMapper.toResponse(saved);

    log.info(
        "OrderCANCELLED orderId={} userId={} previousStatus={} reason={}",
        orderId,
        appUserId,
        current,
        reason);

    orderWebSocketPublisher.publishOrderUpdated(response);

    return response;
  }

  // === Helpers ===
  private boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
