package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;

public record UpdateOrderStatusRequest(OrderStatus status, String note) {}
