package com.cocinadelicia.backend.order.admin.service.impl;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.common.model.enums.RoleName;
import com.cocinadelicia.backend.order.admin.dto.CreateOrderAdminRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminCustomerResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminDetailsResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.admin.dto.OrderFilterRequest;
import com.cocinadelicia.backend.order.admin.dto.OrderStatsResponse;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderCustomerRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderDetailsRequest;
import com.cocinadelicia.backend.order.admin.dto.UpdateOrderItemsRequest;
import com.cocinadelicia.backend.order.admin.mapper.OrderAdminMapper;
import com.cocinadelicia.backend.order.admin.service.OrderAdminService;
import com.cocinadelicia.backend.order.domain.OrderStatusTransitionValidator;
import com.cocinadelicia.backend.order.dto.OrderItemModifierRequest;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.events.OrderWebSocketPublisher;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.model.OrderItemModifier;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.ModifierSelectionMode;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ModifierGroupRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.model.CustomerAddress;
import com.cocinadelicia.backend.user.repository.CustomerAddressRepository;
import com.cocinadelicia.backend.user.repository.RoleRepository;
import com.cocinadelicia.backend.user.repository.UserRepository;
import com.cocinadelicia.backend.user.repository.UserRoleRepository;
import com.cocinadelicia.backend.user.service.CurrentUserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final CustomerAddressRepository customerAddressRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ModifierGroupRepository modifierGroupRepository;
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
              .findByIdForUpdate(it.productVariantId())
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
      BigDecimal baseLineTotal =
          unitPrice.multiply(BigDecimal.valueOf(it.quantity())).setScale(2, RoundingMode.HALF_UP);

      checkAndConsumeStock(variant, it.quantity());

      OrderItem item =
          OrderItem.builder()
              .order(order)
              .product(product)
              .productVariant(variant)
              .productName(product.getName())
              .variantName(variant.getName())
              .unitPrice(unitPrice)
              .quantity(it.quantity())
              .lineTotal(baseLineTotal)
              .build();

      BigDecimal modifiersTotal = applyModifiersAndPricing(item, variant, it.modifiers());
      item.setLineTotal(baseLineTotal.add(modifiersTotal).setScale(2, RoundingMode.HALF_UP));

      subtotal = subtotal.add(item.getLineTotal());

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
    OrderStatusTransitionValidator.validateOrThrow(oldStatus, newStatus);

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

    if (isBlank(chefEmail)) {
      throw new BadRequestException("CHEF_EMAIL_REQUIRED", "El email del chef es obligatorio.");
    }

    AppUser chefUser =
        userRepository
            .findByEmail(chefEmail)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "USER_NOT_FOUND", "Usuario no encontrado con email: " + chefEmail));

    Long chefRoleId =
        roleRepository
            .findByName(RoleName.CHEF)
            .map(r -> r.getId())
            .orElseThrow(
                () -> new NotFoundException("ROLE_NOT_FOUND", "No existe el rol CHEF en la base."));

    boolean isChef = userRoleRepository.existsByUserIdAndRoleId(chefUser.getId(), chefRoleId);
    if (!isChef) {
      throw new BadRequestException("USER_NOT_CHEF", "El usuario no tiene rol CHEF: " + chefEmail);
    }

    order.setAssignedChefEmail(chefEmail);
    CustomerOrder saved = orderRepository.save(order);

    String adminEmail = currentUserService.getCurrentUserEmail();
    log.info("ChefAssigned orderId={} chefEmail={} by={}", orderId, chefEmail, adminEmail);

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
    OrderStatus previousStatus = order.getStatus();
    order.softDelete(deletedBy);
    orderRepository.save(order);

    OrderStatusHistory historyEntry =
        OrderStatusHistory.builder()
            .orderId(orderId)
            .fromStatus(previousStatus)
            .toStatus(OrderStatus.CANCELLED)
            .changedBy(deletedBy)
            .changedAt(Instant.now())
            .reason("Orden cancelada por administrador")
            .build();
    statusHistoryRepository.save(historyEntry);

    log.info("OrderDeleted orderId={} by={}", orderId, deletedBy);
  }

  @Override
  public OrderStatsResponse getStats() {
    LocalDate today = LocalDate.now(ZoneId.of("America/Montevideo"));
    Instant startOfDay = today.atStartOfDay(ZoneId.of("America/Montevideo")).toInstant();
    Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.of("America/Montevideo")).toInstant();

    // Conteo por estado (solo no eliminadas)
    Specification<CustomerOrder> notDeleted = (root, query, cb) -> cb.isNull(root.get("deletedAt"));

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

    // Ingresos de pedidos solicitados hoy (excluye CANCELLED)
    Specification<CustomerOrder> requestedTodayActive =
        notDeleted.and(
            (root, query, cb) -> cb.and(cb.between(root.get("requestedAt"), startOfDay, endOfDay)));
    long requestedTodayActiveCount = orderRepository.count(requestedTodayActive);

    // Canceladas hoy
    Specification<CustomerOrder> CANCELLEDToday =
        (root, query, cb) ->
            cb.and(
                cb.equal(root.get("status"), OrderStatus.CANCELLED),
                cb.between(root.get("updatedAt"), startOfDay, endOfDay));
    long CANCELLEDTodayCount = orderRepository.count(CANCELLEDToday);

    List<CustomerOrder> requestedTodayOrders = orderRepository.findAll(requestedTodayActive);
    BigDecimal revenueTotalToday =
        requestedTodayOrders.stream()
            .map(CustomerOrder::getTotalAmount)
            .reduce(ZERO, BigDecimal::add);

    // Ingresos del día (suma de totalAmount de órdenes entregadas hoy)
    List<CustomerOrder> deliveredOrders = orderRepository.findAll(deliveredToday);
    BigDecimal revenueDeliveredToday =
        deliveredOrders.stream()
            .map(CustomerOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Ticket promedio
    BigDecimal avgTicket = BigDecimal.ZERO;
    if (requestedTodayActiveCount > 0) {
      avgTicket =
          revenueTotalToday.divide(
              BigDecimal.valueOf(requestedTodayActiveCount), 2, java.math.RoundingMode.HALF_UP);
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
        revenueDeliveredToday,
        avgTicket,
        revenueTotalToday);
  }

  @Override
  public OrderAdminResponse updateItems(Long orderId, UpdateOrderItemsRequest request) {
    if (request == null || request.items() == null || request.items().isEmpty()) {
      throw new BadRequestException("ORDER_ITEMS_EMPTY", "Debe agregar al menos un ítem.");
    }

    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    ensureEditable(order);

    for (var it : request.items()) {
      Long orderItemId = it.orderItemId();

      if (orderItemId != null && orderItemId > 0) {
        OrderItem item =
            order.getItems().stream()
                .filter(existing -> existing.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "ORDER_ITEM_NOT_FOUND", "Ítem no encontrado: " + orderItemId));

        if (it.quantity() == 0) {
          order.getItems().remove(item);
          continue;
        }

        ProductVariant variant =
            productVariantRepository
                .findByIdForUpdate(item.getProductVariant().getId())
                .orElseThrow(
                    () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));

        BigDecimal unitPrice =
            priceQueryPort
                .findActivePriceByVariantId(variant.getId())
                .orElseThrow(
                    () ->
                        new BadRequestException(
                            "PRICE_NOT_FOUND",
                            "No hay precio vigente para la variante seleccionada."))
                .setScale(2, RoundingMode.HALF_UP);

        checkAndConsumeStock(variant, it.quantity());

        BigDecimal baseLineTotal =
            unitPrice.multiply(BigDecimal.valueOf(it.quantity())).setScale(2, RoundingMode.HALF_UP);

        item.setQuantity(it.quantity());
        item.setUnitPrice(unitPrice);
        item.setLineTotal(baseLineTotal);
        item.getModifiers().clear();

        BigDecimal modifiersTotal = applyModifiersAndPricing(item, variant, it.modifiers());
        item.setLineTotal(baseLineTotal.add(modifiersTotal).setScale(2, RoundingMode.HALF_UP));
        continue;
      }

      if (it.productVariantId() == null) {
        throw new BadRequestException(
            "PRODUCT_VARIANT_REQUIRED", "Debe indicar productVariantId para agregar un ítem.");
      }

      if (it.quantity() <= 0) {
        throw new BadRequestException(
            "INVALID_QUANTITY", "La cantidad de cada ítem debe ser >= 1.");
      }

      ProductVariant variant =
          productVariantRepository
              .findByIdForUpdate(it.productVariantId())
              .orElseThrow(
                  () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));

      Product product;
      if (it.productId() != null) {
        product =
            productRepository
                .findById(it.productId())
                .orElseThrow(
                    () -> new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado."));
        if (variant.getProduct() == null || !variant.getProduct().getId().equals(product.getId())) {
          throw new BadRequestException(
              "VARIANT_MISMATCH", "La variante no pertenece al producto indicado.");
        }
      } else {
        product = variant.getProduct();
      }

      BigDecimal unitPrice =
          priceQueryPort
              .findActivePriceByVariantId(variant.getId())
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "PRICE_NOT_FOUND",
                          "No hay precio vigente para la variante seleccionada."))
              .setScale(2, RoundingMode.HALF_UP);

      checkAndConsumeStock(variant, it.quantity());

      BigDecimal baseLineTotal =
          unitPrice.multiply(BigDecimal.valueOf(it.quantity())).setScale(2, RoundingMode.HALF_UP);

      OrderItem newItem =
          OrderItem.builder()
              .order(order)
              .product(product)
              .productVariant(variant)
              .productName(product.getName())
              .variantName(variant.getName())
              .unitPrice(unitPrice)
              .quantity(it.quantity())
              .lineTotal(baseLineTotal)
              .build();

      BigDecimal modifiersTotal = applyModifiersAndPricing(newItem, variant, it.modifiers());
      newItem.setLineTotal(baseLineTotal.add(modifiersTotal).setScale(2, RoundingMode.HALF_UP));
      order.addItem(newItem);
    }

    if (order.getItems().isEmpty()) {
      throw new BadRequestException("ORDER_ITEMS_EMPTY", "Debe quedar al menos un ítem.");
    }

    BigDecimal subtotal =
        order.getItems().stream().map(OrderItem::getLineTotal).reduce(ZERO, BigDecimal::add);

    order.setSubtotalAmount(subtotal);
    order.setTaxAmount(ZERO);
    order.setDiscountAmount(ZERO);
    order.setTotalAmount(subtotal);

    CustomerOrder saved = orderRepository.save(order);
    log.info(
        "OrderItemsUpdated orderId={} items={} total={}",
        orderId,
        saved.getItems().size(),
        subtotal);
    return toAdminResponse(saved);
  }

  @Override
  public OrderAdminResponse updateDetails(Long orderId, UpdateOrderDetailsRequest request) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    ensureEditable(order);

    if (request == null) {
      return toAdminResponse(order);
    }

    FulfillmentType newFulfillment =
        request.fulfillment() != null ? request.fulfillment() : order.getFulfillment();

    if (request.shipping() != null && newFulfillment != FulfillmentType.DELIVERY) {
      throw new BadRequestException(
          "SHIPPING_NOT_ALLOWED", "Solo puede agregar envío cuando fulfillment es DELIVERY.");
    }

    if (newFulfillment == FulfillmentType.DELIVERY) {
      var s = request.shipping();
      if (s == null) {
        boolean hasShippingSnapshot =
            !isBlank(order.getShipName())
                && !isBlank(order.getShipPhone())
                && !isBlank(order.getShipLine1())
                && !isBlank(order.getShipCity());
        if (!hasShippingSnapshot) {
          throw new BadRequestException(
              "DELIVERY_ADDRESS_REQUIRED",
              "Complete los datos de envío (nombre, teléfono, dirección y ciudad).");
        }
      } else {
        if (isBlank(s.name()) || isBlank(s.phone()) || isBlank(s.line1()) || isBlank(s.city())) {
          throw new BadRequestException(
              "DELIVERY_ADDRESS_REQUIRED",
              "Complete los datos de envío (nombre, teléfono, dirección y ciudad).");
        }
        order.setShipName(s.name());
        order.setShipPhone(s.phone());
        order.setShipLine1(s.line1());
        order.setShipLine2(s.line2());
        order.setShipCity(s.city());
        order.setShipRegion(s.region());
        order.setShipPostalCode(s.postalCode());
        order.setShipReference(s.reference());
      }
    } else {
      order.setShipName(null);
      order.setShipPhone(null);
      order.setShipLine1(null);
      order.setShipLine2(null);
      order.setShipCity(null);
      order.setShipRegion(null);
      order.setShipPostalCode(null);
      order.setShipReference(null);
    }

    if (request.notes() != null) {
      order.setNotes(request.notes());
    }

    if (request.requestedAt() != null) {
      order.setRequestedAt(request.requestedAt());
    }

    order.setFulfillment(newFulfillment);

    CustomerOrder saved = orderRepository.save(order);
    log.info("OrderDetailsUpdated orderId={} fulfillment={}", orderId, newFulfillment);
    return toAdminResponse(saved);
  }

  @Override
  public OrderAdminResponse updateCustomer(Long orderId, UpdateOrderCustomerRequest request) {
    if (request == null || isBlank(request.userEmail())) {
      throw new BadRequestException("USER_EMAIL_REQUIRED", "El email del cliente es obligatorio.");
    }

    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    ensureEditable(order);

    AppUser user =
        userRepository
            .findByEmail(request.userEmail())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "USER_NOT_FOUND",
                        "Usuario no encontrado con email: " + request.userEmail()));

    order.setUser(user);
    CustomerOrder saved = orderRepository.save(order);
    log.info("OrderCustomerUpdated orderId={} newUserEmail={}", orderId, request.userEmail());
    return toAdminResponse(saved);
  }

  @Override
  public OrderAdminDetailsResponse getOrderDetails(Long id) {
    CustomerOrder order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

    return new OrderAdminDetailsResponse(
        order.getId(),
        order.getStatus(),
        order.getFulfillment(),
        order.getCurrency(),
        order.getSubtotalAmount(),
        order.getTaxAmount(),
        order.getDiscountAmount(),
        order.getTotalAmount(),
        order.getShipName(),
        order.getShipPhone(),
        order.getShipLine1(),
        order.getShipLine2(),
        order.getShipCity(),
        order.getShipRegion(),
        order.getShipPostalCode(),
        order.getShipReference(),
        order.getNotes(),
        order.getAssignedChefEmail(),
        order.getRequestedAt(),
        order.getDeliveredAt(),
        order.getItems().stream().map(OrderAdminMapper::toItemResponse).toList(),
        order.getCreatedAt(),
        order.getUpdatedAt(),
        order.getDeletedAt(),
        order.getDeletedBy());
  }

  @Override
  public OrderAdminCustomerResponse getOrderCustomer(Long orderId) {
    CustomerOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

    AppUser user = order.getUser();
    List<CustomerAddress> addresses = customerAddressRepository.findByUser_Id(user.getId());

    var addressDtos =
        addresses.stream()
            .map(
                address ->
                    new OrderAdminCustomerResponse.CustomerAddressResponse(
                        address.getId(),
                        address.getLabel(),
                        address.getLine1(),
                        address.getLine2(),
                        address.getCity(),
                        address.getRegion(),
                        address.getPostalCode(),
                        address.getReference()))
            .toList();

    var shippingSnapshot =
        new OrderAdminCustomerResponse.ShippingSnapshotResponse(
            order.getShipName(),
            order.getShipPhone(),
            order.getShipLine1(),
            order.getShipLine2(),
            order.getShipCity(),
            order.getShipRegion(),
            order.getShipPostalCode(),
            order.getShipReference());

    return new OrderAdminCustomerResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getPhone(),
        addressDtos,
        shippingSnapshot);
  }

  private void ensureEditable(CustomerOrder order) {
    OrderStatus status = order.getStatus();
    if (status != OrderStatus.CREATED
        && status != OrderStatus.CONFIRMED
        && status != OrderStatus.PREPARING) {
      throw new BadRequestException(
          "ORDER_NOT_EDITABLE",
          "Solo se pueden editar órdenes en estado CREATED, CONFIRMED o PREPARING.");
    }
  }

  private OrderAdminResponse toAdminResponse(CustomerOrder order) {
    List<OrderStatusHistory> history =
        statusHistoryRepository.findByOrderIdOrderByChangedAtDesc(order.getId());
    return OrderAdminMapper.toAdminResponse(order, history);
  }

  private Specification<CustomerOrder> buildSpecification(OrderFilterRequest filters) {
    List<Specification<CustomerOrder>> specs = new ArrayList<>();
    ZoneId zone = ZoneId.of("America/Montevideo");

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

    if (filters.getFulfillment() != null) {
      specs.add((root, query, cb) -> cb.equal(root.get("fulfillment"), filters.getFulfillment()));
    }

    if (filters.getCreatedAfter() != null) {
      Instant start = filters.getCreatedAfter().atStartOfDay(zone).toInstant();
      specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
    }

    if (filters.getCreatedBefore() != null) {
      Instant end = filters.getCreatedBefore().plusDays(1).atStartOfDay(zone).toInstant();
      specs.add((root, query, cb) -> cb.lessThan(root.get("createdAt"), end));
    }

    if (filters.getRequestedAfter() != null) {
      Instant start = filters.getRequestedAfter().atStartOfDay(zone).toInstant();
      specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("requestedAt"), start));
    }

    if (filters.getRequestedBefore() != null) {
      Instant end = filters.getRequestedBefore().plusDays(1).atStartOfDay(zone).toInstant();
      specs.add((root, query, cb) -> cb.lessThan(root.get("requestedAt"), end));
    }

    if (filters.getMinAmount() != null) {
      specs.add(
          (root, query, cb) ->
              cb.greaterThanOrEqualTo(root.get("totalAmount"), filters.getMinAmount()));
    }

    if (filters.getMaxAmount() != null) {
      specs.add(
          (root, query, cb) ->
              cb.lessThanOrEqualTo(root.get("totalAmount"), filters.getMaxAmount()));
    }

    String q = filters.getQ();
    if (q != null) {
      String trimmed = q.trim();
      if (!trimmed.isEmpty()) {
        String normalized = trimmed.toLowerCase();
        String likeValue = "%" + normalized + "%";

        specs.add(
            (root, query, cb) -> {
              List<Predicate> orPredicates = new ArrayList<>();
              orPredicates.add(cb.like(cb.lower(root.get("user").get("email")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("notes")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("shipName")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("shipPhone")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("shipLine1")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("shipCity")), likeValue));
              orPredicates.add(cb.like(cb.lower(root.get("shipReference")), likeValue));

              if (normalized.matches("\\d+")) {
                try {
                  Long orderId = Long.valueOf(normalized);
                  orPredicates.add(cb.equal(root.get("id"), orderId));
                } catch (NumberFormatException ignored) {
                  // ignore invalid numeric overflow
                }
              }

              return cb.or(orPredicates.toArray(new Predicate[0]));
            });
      }
    }

    if (filters.getProductId() != null || filters.getProductVariantId() != null) {
      specs.add(
          (root, query, cb) -> {
            query.distinct(true);
            Join<CustomerOrder, OrderItem> items = root.join("items", JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            if (filters.getProductId() != null) {
              predicates.add(cb.equal(items.get("product").get("id"), filters.getProductId()));
            }
            if (filters.getProductVariantId() != null) {
              predicates.add(
                  cb.equal(items.get("productVariant").get("id"), filters.getProductVariantId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
          });
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

  private BigDecimal applyModifiersAndPricing(
      OrderItem item, ProductVariant variant, List<OrderItemModifierRequest> modifierRequests) {
    int itemQuantity = item.getQuantity() != null ? item.getQuantity() : 1;

    List<ModifierGroup> groups =
        modifierGroupRepository.findByProductVariant_IdAndActiveTrueOrderBySortOrderAscIdAsc(
            variant.getId());

    Map<Long, ModifierGroup> groupById = new HashMap<>();
    Map<Long, ModifierOption> optionById = new HashMap<>();
    for (ModifierGroup g : groups) {
      groupById.put(g.getId(), g);
      if (g.getOptions() != null) {
        g.getOptions().stream()
            .filter(ModifierOption::isActive)
            .forEach(opt -> optionById.put(opt.getId(), opt));
      }
    }

    if (groups.isEmpty() && modifierRequests != null && !modifierRequests.isEmpty()) {
      throw new BadRequestException(
          "MODIFIER_NOT_ALLOWED", "El producto/variante no admite modificadores.");
    }

    record SelectedOption(ModifierOption option, int quantity) {}

    Map<ModifierGroup, List<SelectedOption>> selections = new HashMap<>();
    if (modifierRequests != null) {
      for (var modReq : modifierRequests) {
        ModifierOption option = optionById.get(modReq.modifierOptionId());
        if (option == null) {
          throw new BadRequestException(
              "MODIFIER_OPTION_INVALID", "La opción de modificador indicada no es válida.");
        }
        ModifierGroup group = option.getModifierGroup();
        selections
            .computeIfAbsent(group, g -> new ArrayList<>())
            .add(new SelectedOption(option, modReq.quantity() == null ? 1 : modReq.quantity()));
      }
    }

    for (ModifierGroup group : groups) {
      boolean hasSelection = selections.containsKey(group) && !selections.get(group).isEmpty();
      if (!hasSelection
          && group.getDefaultOption() != null
          && group.getDefaultOption().isActive()) {
        int defaultQty =
            group.getSelectionMode() == ModifierSelectionMode.QTY
                    && group.getRequiredTotalQty() != null
                ? group.getRequiredTotalQty()
                : 1;
        selections
            .computeIfAbsent(group, g -> new ArrayList<>())
            .add(new SelectedOption(group.getDefaultOption(), defaultQty));
      }
    }

    BigDecimal modifiersTotal = BigDecimal.ZERO;

    for (ModifierGroup group : groups) {
      List<SelectedOption> selected = selections.getOrDefault(group, List.of());
      int optionCount = selected.size();
      int totalQty = selected.stream().mapToInt(SelectedOption::quantity).sum();

      if (group.getMinSelect() > 0 && optionCount < group.getMinSelect()) {
        throw new BadRequestException(
            "MODIFIER_MIN_SELECT",
            String.format(
                "El grupo %s requiere al menos %d opción(es).",
                group.getName(), group.getMinSelect()));
      }
      if (group.getMaxSelect() > 0 && optionCount > group.getMaxSelect()) {
        throw new BadRequestException(
            "MODIFIER_MAX_SELECT",
            String.format(
                "El grupo %s permite como máximo %d opción(es).",
                group.getName(), group.getMaxSelect()));
      }

      boolean hasExclusive = selected.stream().anyMatch(sel -> sel.option().isExclusive());
      if (hasExclusive && optionCount > 1) {
        throw new BadRequestException(
            "MODIFIER_EXCLUSIVE",
            String.format(
                "La opción exclusiva en %s no puede combinarse con otras.", group.getName()));
      }

      switch (group.getSelectionMode()) {
        case SINGLE -> {
          if (optionCount > 1) {
            throw new BadRequestException(
                "MODIFIER_SINGLE",
                String.format("El grupo %s solo permite una opción.", group.getName()));
          }
          if (selected.stream().anyMatch(sel -> sel.quantity() != 1)) {
            throw new BadRequestException(
                "MODIFIER_SINGLE_QTY",
                String.format("El grupo %s admite cantidad 1 por opción.", group.getName()));
          }
        }
        case MULTI -> {
          if (selected.stream().anyMatch(sel -> sel.quantity() != 1)) {
            throw new BadRequestException(
                "MODIFIER_MULTI_QTY",
                String.format("El grupo %s solo admite cantidad 1 por opción.", group.getName()));
          }
        }
        case QTY -> {
          if (group.getRequiredTotalQty() != null && totalQty != group.getRequiredTotalQty()) {
            throw new BadRequestException(
                "MODIFIER_REQUIRED_TOTAL",
                String.format(
                    "El grupo %s requiere un total de %d unidades.",
                    group.getName(), group.getRequiredTotalQty()));
          }
        }
        default -> {}
      }

      for (SelectedOption sel : selected) {
        ModifierOption option = sel.option();
        int requestedQty = sel.quantity();
        if (requestedQty <= 0) {
          throw new BadRequestException(
              "MODIFIER_QTY_INVALID", "La cantidad del modificador debe ser mayor a 0.");
        }

        BigDecimal optionUnitPrice = null;
        BigDecimal priceDelta = option.getPriceDelta();
        BigDecimal optionTotal = BigDecimal.ZERO;

        if (priceDelta != null) {
          optionTotal =
              priceDelta
                  .multiply(BigDecimal.valueOf(requestedQty))
                  .multiply(BigDecimal.valueOf(itemQuantity))
                  .setScale(2, RoundingMode.HALF_UP);
        }

        if (option.getLinkedProductVariant() != null) {
          ProductVariant linkedVariant =
              productVariantRepository
                  .findByIdForUpdate(option.getLinkedProductVariant().getId())
                  .orElseThrow(
                      () ->
                          new NotFoundException(
                              "LINKED_VARIANT_NOT_FOUND", "Variante linkeada no encontrada."));

          BigDecimal linkedPrice =
              priceQueryPort
                  .findActivePriceByVariantId(linkedVariant.getId())
                  .orElseThrow(
                      () ->
                          new BadRequestException(
                              "LINKED_PRICE_NOT_FOUND",
                              "No hay precio vigente para la variante de modificador."));
          optionUnitPrice = linkedPrice.setScale(2, RoundingMode.HALF_UP);
          BigDecimal linkedTotal =
              optionUnitPrice
                  .multiply(BigDecimal.valueOf(requestedQty))
                  .multiply(BigDecimal.valueOf(itemQuantity))
                  .setScale(2, RoundingMode.HALF_UP);
          optionTotal = optionTotal.add(linkedTotal);

          checkAndConsumeStock(linkedVariant, requestedQty * itemQuantity);
        }

        modifiersTotal = modifiersTotal.add(optionTotal);

        OrderItemModifier modifierEntity =
            OrderItemModifier.builder()
                .orderItem(item)
                .modifierOption(option)
                .quantity(requestedQty)
                .optionNameSnapshot(option.getName())
                .priceDeltaSnapshot(priceDelta)
                .unitPriceSnapshot(optionUnitPrice)
                .totalPriceSnapshot(optionTotal)
                .linkedProductVariantIdSnapshot(
                    option.getLinkedProductVariant() != null
                        ? option.getLinkedProductVariant().getId()
                        : null)
                .build();
        item.getModifiers().add(modifierEntity);
      }
    }

    return modifiersTotal.setScale(2, RoundingMode.HALF_UP);
  }

  private void checkAndConsumeStock(ProductVariant variant, int requiredQty) {
    if (variant == null || !variant.isManagesStock()) return;
    if (variant.getStockQuantity() < requiredQty) {
      throw new BadRequestException(
          "OUT_OF_STOCK",
          String.format(
              "No hay stock suficiente para la variante %s (solicitado %d, disponible %d)",
              variant.getName(), requiredQty, variant.getStockQuantity()));
    }
    variant.setStockQuantity(variant.getStockQuantity() - requiredQty);
  }

  private boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
