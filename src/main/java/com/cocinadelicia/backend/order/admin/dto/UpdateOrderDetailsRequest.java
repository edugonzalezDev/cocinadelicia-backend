package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.order.dto.ShippingAddressRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.time.Instant;

@Schema(description = "Request para actualizar detalles del pedido (Admin)")
public record UpdateOrderDetailsRequest(
    @Schema(description = "Tipo de cumplimiento", example = "DELIVERY") FulfillmentType fulfillment,
    @Schema(description = "Dirección de envío si aplica") @Valid ShippingAddressRequest shipping,
    @Schema(description = "Notas del pedido", example = "Entregar en horario de oficina")
        String notes,
    @Schema(
            description = "Fecha/hora solicitada de entrega o retiro",
            example = "2026-01-25T20:00:00Z")
        Instant requestedAt) {}
