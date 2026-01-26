package com.cocinadelicia.backend.order.admin.mapper;

import com.cocinadelicia.backend.order.admin.dto.OrderAdminResponse;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import com.cocinadelicia.backend.order.model.CustomerOrder;
import com.cocinadelicia.backend.order.model.OrderStatusHistory;
import java.util.List;

public final class OrderAdminMapper {

  private OrderAdminMapper() {}

  public static OrderAdminResponse toAdminResponse(
      CustomerOrder order, List<OrderStatusHistory> statusHistory) {

    List<OrderItemResponse> itemDtos =
        order.getItems().stream().map(OrderAdminMapper::toItemResponse).toList();

    List<OrderAdminResponse.StatusHistoryEntry> historyDtos =
        statusHistory.stream().map(OrderAdminMapper::toHistoryEntry).toList();

    return new OrderAdminResponse(
        order.getId(),
        order.getUser().getId(),
        order.getUser().getEmail(),
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
        order.getAssignedChefEmail(),
        itemDtos,
        order.getCreatedAt(),
        order.getUpdatedAt(),
        order.getDeletedAt(),
        order.getDeletedBy(),
        historyDtos);
  }

  private static OrderItemResponse toItemResponse(
      com.cocinadelicia.backend.order.model.OrderItem item) {
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

  private static OrderAdminResponse.StatusHistoryEntry toHistoryEntry(OrderStatusHistory history) {
    return new OrderAdminResponse.StatusHistoryEntry(
        history.getFromStatus(),
        history.getToStatus(),
        history.getChangedBy(),
        history.getChangedAt(),
        history.getReason());
  }
}
