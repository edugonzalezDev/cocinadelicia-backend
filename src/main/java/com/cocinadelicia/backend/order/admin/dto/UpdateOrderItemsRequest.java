package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request para reemplazar todos los ítems de una orden (Admin)")
public record UpdateOrderItemsRequest(
    @Schema(description = "Lista completa de ítems", required = true)
        @NotEmpty(message = "Debe incluir al menos un ítem") @Valid List<OrderItemRequest> items) {}
