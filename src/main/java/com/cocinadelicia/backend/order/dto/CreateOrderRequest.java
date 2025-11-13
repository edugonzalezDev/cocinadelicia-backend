package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Payload para crear un nuevo pedido")
public record CreateOrderRequest(

  @Schema(
    description = "Modalidad de cumplimiento del pedido",
    example = "DELIVERY",
    allowableValues = {"PICKUP", "DELIVERY"}
  )
  @NotNull(message = "fulfillment es obligatorio")
  FulfillmentType fulfillment,

  @Schema(
    description = "Ítems del pedido. Debe contener al menos un ítem.",
    example = """
            [
              { "productId": 1, "productVariantId": 10, "quantity": 2 },
              { "productId": 2, "productVariantId": 20, "quantity": 1 }
            ]
            """
  )
  @NotEmpty(message = "items no puede estar vacío")
  @Size(max = 50, message = "items excede el máximo permitido (50)")
  @Valid
  List<OrderItemRequest> items,

  @Schema(
    description = "Notas opcionales del cliente",
    example = "Sin cebolla y poco picante"
  )
  @Size(max = 500, message = "notes excede el máximo permitido (500)")
  String notes,

  // Solo requerido si fulfillment == DELIVERY (se valida en el servicio)
  @Schema(
    description = "Datos de envío, requeridos cuando fulfillment = DELIVERY"
  )
  @Valid
  ShippingAddressRequest shipping
) {}
