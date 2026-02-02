package com.cocinadelicia.backend.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.order.dto.CreateOrderRequest;
import com.cocinadelicia.backend.order.dto.OrderItemModifierRequest;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.events.OrderWebSocketPublisher;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.repository.CustomerOrderRepository;
import com.cocinadelicia.backend.order.repository.OrderItemRepository;
import com.cocinadelicia.backend.order.repository.OrderStatusHistoryRepository;
import com.cocinadelicia.backend.order.service.impl.OrderServiceImpl;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.ModifierSelectionMode;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ModifierGroupRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import com.cocinadelicia.backend.user.model.AppUser;
import com.cocinadelicia.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceModifierTest {

  @Mock private UserRepository userRepository;
  @Mock private ProductRepository productRepository;
  @Mock private ProductVariantRepository productVariantRepository;
  @Mock private ModifierGroupRepository modifierGroupRepository;
  @Mock private CustomerOrderRepository customerOrderRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private OrderStatusHistoryRepository statusHistoryRepository;
  @Mock private com.cocinadelicia.backend.order.port.PriceQueryPort priceQueryPort;
  @Mock private OrderWebSocketPublisher orderWebSocketPublisher;

  private OrderServiceImpl orderService;

  @BeforeEach
  void setup() {
    orderService =
        new OrderServiceImpl(
            userRepository,
            productRepository,
            productVariantRepository,
            modifierGroupRepository,
            customerOrderRepository,
            orderItemRepository,
            statusHistoryRepository,
            priceQueryPort,
            orderWebSocketPublisher);
  }

  @Test
  void createOrder_withModifiers_appliesPricingStockAndSnapshots() {
    AppUser user = AppUser.builder().id(7L).email("user@test.com").cognitoUserId("cog").build();
    when(userRepository.findById(7L)).thenReturn(Optional.of(user));

    Product product = Product.builder().id(1L).name("Milanesa").build();
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    ProductVariant baseVariant =
        ProductVariant.builder()
            .id(11L)
            .product(product)
            .name("Grande")
            .managesStock(true)
            .stockQuantity(10)
            .build();
    when(productVariantRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(baseVariant));

    ProductVariant linkedVariant =
        ProductVariant.builder()
            .id(22L)
            .product(product)
            .name("Guarnición")
            .managesStock(true)
            .stockQuantity(5)
            .build();
    when(productVariantRepository.findByIdForUpdate(22L)).thenReturn(Optional.of(linkedVariant));

    ModifierGroup group =
        ModifierGroup.builder()
            .id(100L)
            .productVariant(baseVariant)
            .name("Guarniciones")
            .minSelect(0)
            .maxSelect(3)
            .selectionMode(ModifierSelectionMode.QTY)
            .requiredTotalQty(3)
            .sortOrder(0)
            .active(true)
            .build();

    ModifierOption optDelta =
        ModifierOption.builder()
            .id(201L)
            .modifierGroup(group)
            .name("Puré")
            .sortOrder(0)
            .active(true)
            .priceDelta(new BigDecimal("10.00"))
            .exclusive(false)
            .build();

    ModifierOption optLinked =
        ModifierOption.builder()
            .id(202L)
            .modifierGroup(group)
            .name("Ensalada")
            .sortOrder(1)
            .active(true)
            .exclusive(false)
            .linkedProductVariant(linkedVariant)
            .build();

    group.setOptions(List.of(optDelta, optLinked));
    when(modifierGroupRepository.findByProductVariant_IdAndActiveTrueOrderBySortOrderAscIdAsc(11L))
        .thenReturn(List.of(group));

    when(priceQueryPort.findActivePriceByVariantId(11L))
        .thenReturn(Optional.of(new BigDecimal("100.00")));
    when(priceQueryPort.findActivePriceByVariantId(22L))
        .thenReturn(Optional.of(new BigDecimal("5.00")));

    ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
    when(customerOrderRepository.save(orderCaptor.capture()))
        .thenAnswer(
            invocation -> {
              CustomerOrder o = invocation.getArgument(0);
              o.setId(500L);
              o.getItems().forEach(i -> i.setId(501L));
              return o;
            });

    CreateOrderRequest request =
        new CreateOrderRequest(
            FulfillmentType.PICKUP,
            List.of(
                new OrderItemRequest(
                    1L,
                    11L,
                    1,
                    List.of(
                        new OrderItemModifierRequest(201L, 2), // price delta 10 *2
                        new OrderItemModifierRequest(202L, 1)))),
            "",
            null,
            null);

    var response = orderService.createOrder(request, 7L);

    CustomerOrder persisted = orderCaptor.getValue();
    assertEquals(500L, response.id());
    assertEquals(1, response.items().size());
    var itemResponse = response.items().get(0);
    assertEquals(2, itemResponse.modifiers().size());

    // Precios: base 100 + delta (10*2) + linked (5*1) = 125
    assertEquals(new BigDecimal("125.00"), itemResponse.lineTotal());
    assertEquals(new BigDecimal("125.00"), response.totalAmount());

    // Stock descontado
    assertEquals(9, baseVariant.getStockQuantity());
    assertEquals(4, linkedVariant.getStockQuantity());

    // Snapshots
    var modDelta =
        itemResponse.modifiers().stream()
            .filter(m -> m.modifierOptionId().equals(201L))
            .findFirst()
            .orElseThrow();
    assertEquals(new BigDecimal("10.00"), modDelta.priceDeltaSnapshot());
    var modLinked =
        itemResponse.modifiers().stream()
            .filter(m -> m.modifierOptionId().equals(202L))
            .findFirst()
            .orElseThrow();
    assertEquals(new BigDecimal("5.00"), modLinked.unitPriceSnapshot());

    verify(orderWebSocketPublisher).publishOrderUpdated(any());
  }

  @Test
  void createOrder_requiredTotalQtyMismatch_throwsBadRequest() {
    AppUser user = AppUser.builder().id(7L).email("user@test.com").cognitoUserId("cog").build();
    when(userRepository.findById(7L)).thenReturn(Optional.of(user));

    Product product = Product.builder().id(1L).name("Empanadas").build();
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    ProductVariant baseVariant =
        ProductVariant.builder()
            .id(11L)
            .product(product)
            .name("Pack x6")
            .managesStock(true)
            .stockQuantity(10)
            .build();
    when(productVariantRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(baseVariant));

    ModifierGroup group =
        ModifierGroup.builder()
            .id(300L)
            .productVariant(baseVariant)
            .name("Sabores")
            .minSelect(1)
            .maxSelect(3)
            .selectionMode(ModifierSelectionMode.QTY)
            .requiredTotalQty(6)
            .sortOrder(0)
            .active(true)
            .build();

    ModifierOption carne =
        ModifierOption.builder()
            .id(301L)
            .modifierGroup(group)
            .name("Carne")
            .sortOrder(0)
            .active(true)
            .build();
    group.setOptions(List.of(carne));

    when(modifierGroupRepository.findByProductVariant_IdAndActiveTrueOrderBySortOrderAscIdAsc(11L))
        .thenReturn(List.of(group));
    when(priceQueryPort.findActivePriceByVariantId(anyLong()))
        .thenReturn(Optional.of(new BigDecimal("100.00")));

    CreateOrderRequest request =
        new CreateOrderRequest(
            FulfillmentType.PICKUP,
            List.of(
                new OrderItemRequest(1L, 11L, 1, List.of(new OrderItemModifierRequest(301L, 1)))),
            null,
            null,
            null);

    assertThrows(BadRequestException.class, () -> orderService.createOrder(request, 7L));
    // No persiste orden
    verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
  }
}
