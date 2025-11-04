package com.cocinadelicia.backend.order.service.impl;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.mapper.OrderMapper;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderItemRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;

  private final CustomerOrderRepository customerOrderRepository;
  private final OrderItemRepository
      orderItemRepository; // (no lo usamos explícitamente; Cascade en Order)
  private final PriceQueryPort priceQueryPort;

  private static final BigDecimal ZERO = new BigDecimal("0.00");

  @Override
  public OrderResponse createOrder(CreateOrderRequest request, Long appUserId) {
    // 1) Validaciones mínimas del payload
    if (request.items() == null || request.items().isEmpty()) {
      throw new BadRequestException("El pedido debe tener al menos un ítem.");
    }
    request
        .items()
        .forEach(
            it -> {
              if (it.quantity() <= 0) {
                throw new BadRequestException("La cantidad de cada ítem debe ser >= 1.");
              }
            });

    if (request.fulfillment() == null) {
      throw new BadRequestException("El tipo de fulfillment es obligatorio.");
    }
    if (request.fulfillment() == FulfillmentType.DELIVERY) {
      if (request.shipping() == null) {
        throw new BadRequestException("Datos de envío requeridos para DELIVERY.");
      }
      // Nota: a esta altura ya pasó validaciones @NotBlank de ShippingAddressRequest
    }

    // 2) Usuario (dueño del pedido)
    AppUser user =
        userRepository
            .findById(appUserId)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado: id=" + appUserId));

    // 3) Crear entidad Order base
    CustomerOrder order =
        CustomerOrder.builder()
            .user(user)
            .status(OrderStatus.CREATED)
            .fulfillment(request.fulfillment())
            // currency default en entidad = UYU
            .notes(request.notes())
            .build();

    // 4) Snapshot shipping si DELIVERY
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

    // 5) Armar items: validar producto/variante, obtener precio vigente y calcular line totals
    BigDecimal subtotal = ZERO;

    for (OrderItemRequest it : request.items()) {
      Product product =
          productRepository
              .findById(it.productId())
              .orElseThrow(
                  () -> new NotFoundException("Producto no encontrado: id=" + it.productId()));

      ProductVariant variant =
          productVariantRepository
              .findById(it.productVariantId())
              .orElseThrow(
                  () ->
                      new NotFoundException("Variante no encontrada: id=" + it.productVariantId()));

      // (Opcional) validar que variant.getProduct().getId() == productId
      if (variant.getProduct() == null || !variant.getProduct().getId().equals(product.getId())) {
        throw new BadRequestException("La variante no pertenece al producto especificado.");
      }

      BigDecimal unitPrice =
          priceQueryPort
              .findActivePriceByVariantId(variant.getId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "No hay precio vigente para la variante: id=" + variant.getId()));

      unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);

      BigDecimal lineTotal =
          unitPrice.multiply(BigDecimal.valueOf(it.quantity())).setScale(2, RoundingMode.HALF_UP);

      subtotal = subtotal.add(lineTotal);

      OrderItem item =
          OrderItem.builder()
              .order(order) // se re-asigna en addItem, pero no molesta
              .product(product)
              .productVariant(variant)
              .productName(product.getName())
              .variantName(variant.getName())
              .unitPrice(unitPrice)
              .quantity(it.quantity())
              .lineTotal(lineTotal)
              .build();

      order.addItem(item); // mantiene consistencia en ambos lados
    }

    // 6) Totales (por ahora tax=0 y discount=0; ajustaremos en sprints futuros)
    order.setSubtotalAmount(subtotal);
    order.setTaxAmount(ZERO);
    order.setDiscountAmount(ZERO);
    order.setTotalAmount(subtotal);

    // 7) Persistir (cascada guarda los items)
    CustomerOrder saved = customerOrderRepository.save(order);

    // 8) Responder DTO
    return OrderMapper.toResponse(saved);
  }

  // ==== Métodos de lectura (para que el controller compile). Implementación simple ====

  @Override
  public OrderResponse getOrderById(Long orderId, Long appUserId) {
    CustomerOrder o =
        customerOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new NotFoundException("Pedido no encontrado: id=" + orderId));

    if (!o.getUser().getId().equals(appUserId)) {
      throw new NotFoundException("Pedido no pertenece al usuario."); // 404 para no filtrar info
    }
    return OrderMapper.toResponse(o);
  }

  @Override
  public Page<OrderResponse> getMyOrders(Long appUserId, Pageable pageable) {
    // El sort se define en el controller con @PageableDefault(createdAt,DESC)
    return customerOrderRepository.findByUser_Id(appUserId, pageable).map(OrderMapper::toResponse);
  }
}
