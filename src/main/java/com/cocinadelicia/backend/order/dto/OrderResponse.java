package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    Long id,
    OrderStatus status,
    FulfillmentType fulfillment,
    CurrencyCode currency,
    BigDecimal subtotalAmount,
    BigDecimal taxAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,

    // Snapshot envío (si DELIVERY)
    String shipName,
    String shipPhone,
    String shipLine1,
    String shipLine2,
    String shipCity,
    String shipRegion,
    String shipPostalCode,
    String shipReference,
    String notes,
    List<OrderItemResponse> items,
    Instant createdAt,
    Instant updatedAt) {}
