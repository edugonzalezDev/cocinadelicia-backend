package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(
    @NotNull(message = "fulfillment es obligatorio") FulfillmentType fulfillment,
    @NotEmpty(message = "items no puede estar vacío") @Valid List<OrderItemRequest> items,
    String notes,

    // Solo requerido si fulfillment == DELIVERY (se valida en el servicio)
    @Valid ShippingAddressRequest shipping) {}
