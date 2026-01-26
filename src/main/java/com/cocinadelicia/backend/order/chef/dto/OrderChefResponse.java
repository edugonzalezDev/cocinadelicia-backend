package com.cocinadelicia.backend.order.chef.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import com.cocinadelicia.backend.order.dto.OrderItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Respuesta de orden para chef (información esencial de preparación)")
public record OrderChefResponse(
    @Schema(description = "ID del pedido", example = "42") Long id,
    @Schema(description = "Estado actual", example = "PREPARING") OrderStatus status,
    @Schema(description = "Notas especiales del pedido") String notes,
    @Schema(description = "Ítems a preparar") List<OrderItemResponse> items,
    @Schema(description = "Fecha de creación") Instant createdAt,
    @Schema(description = "Fecha de última actualización") Instant updatedAt,
    @Schema(description = "Hora solicitada de entrega/retiro") Instant requestedAt) {}
