package com.cocinadelicia.backend.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
    @NotNull(message = "productId es obligatorio") Long productId,
    @NotNull(message = "productVariantId es obligatorio") Long productVariantId,
    @Min(value = 1, message = "quantity debe ser >= 1") int quantity) {}
