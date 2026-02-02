package com.cocinadelicia.backend.order.mapper;

import com.cocinadelicia.backend.order.dto.OrderItemModifierResponse;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import com.cocinadelicia.backend.order.dto.OrderResponse;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
import java.util.List;

public final class OrderMapper {

  private OrderMapper() {}

  public static OrderResponse toResponse(CustomerOrder order) {
    List<OrderItemResponse> itemDtos =
        order.getItems().stream().map(OrderMapper::toItemResponse).toList();

    return new OrderResponse(
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
        order.getRequestedAt(),
        order.getDeliveredAt(),
        itemDtos,
        order.getCreatedAt(),
        order.getUpdatedAt());
  }

  private static OrderItemResponse toItemResponse(OrderItem item) {
    List<OrderItemModifierResponse> modifierResponses =
        item.getModifiers() == null
            ? List.of()
            : item.getModifiers().stream()
                .map(
                    m ->
                        new OrderItemModifierResponse(
                            m.getId(),
                            m.getModifierOption().getId(),
                            m.getOptionNameSnapshot(),
                            m.getQuantity(),
                            m.getPriceDeltaSnapshot(),
                            m.getUnitPriceSnapshot(),
                            m.getTotalPriceSnapshot(),
                            m.getLinkedProductVariantIdSnapshot()))
                .toList();

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
}
