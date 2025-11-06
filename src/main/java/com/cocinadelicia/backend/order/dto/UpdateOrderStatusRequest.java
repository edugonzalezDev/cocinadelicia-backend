// src/main/java/com/cocinadelicia/backend/order/dto/UpdateOrderStatusRequest.java
package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "El estado es obligatorio") OrderStatus status, String note) {}
