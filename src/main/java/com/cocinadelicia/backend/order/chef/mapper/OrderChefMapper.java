package com.cocinadelicia.backend.order.chef.mapper;

import com.cocinadelicia.backend.order.chef.dto.OrderChefResponse;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderItem;
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
    return new OrderItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProductVariant().getId(),
        item.getProductName(),
        item.getVariantName(),
        item.getUnitPrice(),
        item.getQuantity(),
        item.getLineTotal());
  }
}
