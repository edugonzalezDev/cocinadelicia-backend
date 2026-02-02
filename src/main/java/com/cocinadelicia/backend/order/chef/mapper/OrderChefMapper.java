package com.cocinadelicia.backend.order.chef.mapper;

import com.cocinadelicia.backend.order.chef.dto.OrderChefResponse;
import com.cocinadelicia.backend.order.dto.OrderItemModifierResponse;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import com.cocinadelicia.backend.order.model.OrderItemModifier;
import java.util.List;

public final class OrderChefMapper {

  private OrderChefMapper() {}

  public static OrderChefResponse toChefResponse(CustomerOrder order) {
    List<OrderItemResponse> itemDtos =
        order.getItems().stream().map(OrderChefMapper::toItemResponse).toList();

    return new OrderChefResponse(
        order.getId(),
        order.getStatus(),
        order.getNotes(),
        itemDtos,
        order.getCreatedAt(),
        order.getUpdatedAt(),
        order.getRequestedAt());
  }

  private static OrderItemResponse toItemResponse(OrderItem item) {
    List<OrderItemModifierResponse> modifierResponses =
        item.getModifiers() == null
            ? List.of()
            : item.getModifiers().stream().map(OrderChefMapper::toModifierResponse).toList();

    return new OrderItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProductVariant().getId(),
        item.getProductName(),
        item.getVariantName(),
        item.getUnitPrice(),
        item.getQuantity(),
        item.getLineTotal(),
        modifierResponses);
  }

  private static OrderItemModifierResponse toModifierResponse(OrderItemModifier m) {
    return new OrderItemModifierResponse(
        m.getId(),
        m.getModifierOption().getId(),
        m.getOptionNameSnapshot(),
        m.getQuantity(),
        m.getPriceDeltaSnapshot(),
        m.getUnitPriceSnapshot(),
        m.getTotalPriceSnapshot(),
        m.getLinkedProductVariantIdSnapshot());
  }
}
