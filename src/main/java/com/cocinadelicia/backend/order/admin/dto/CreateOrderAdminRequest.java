package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.order.dto.OrderItemRequest;
import com.cocinadelicia.backend.order.dto.ShippingAddressRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Schema(description = "Request para que Admin cree una orden en nombre de un cliente")
public record CreateOrderAdminRequest(
    @Schema(description = "Email del cliente para quien se crea la orden", example = "cliente@example.com", required = true)
        @NotBlank(message = "El email del cliente es obligatorio")
        @Email(message = "Debe ser un email válido")
        String userEmail,

    @Schema(description = "Tipo de cumplimiento del pedido", example = "DELIVERY", required = true)
        @NotNull(message = "El tipo de fulfillment es obligatorio")
        FulfillmentType fulfillment,

    @Schema(description = "Lista de items del pedido", required = true)
        @NotEmpty(message = "Debe incluir al menos un item")
        @Valid
        List<OrderItemRequest> items,

    @Schema(description = "Datos de envío (requerido si fulfillment=DELIVERY)")
        @Valid
        ShippingAddressRequest shipping,

    @Schema(description = "Notas adicionales del pedido", example = "Entregar en horario de oficina")
        String notes,

    @Schema(description = "Fecha/hora deseada de entrega o retiro", example = "2026-01-25T20:00:00Z")
        Instant requestedAt,

    @Schema(description = "Email del chef asignado (opcional)", example = "chef@example.com")
        @Email(message = "Debe ser un email válido")
        String assignedChefEmail) {}
