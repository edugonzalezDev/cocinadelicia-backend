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
  private final OrderItemRepository orderItemRepository; // no se usa explícitamente
  private final PriceQueryPort priceQueryPort;

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

    // 8️⃣ Devolver respuesta DTO
    return OrderMapper.toResponse(saved);
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

  // === Helpers ===
  private boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
