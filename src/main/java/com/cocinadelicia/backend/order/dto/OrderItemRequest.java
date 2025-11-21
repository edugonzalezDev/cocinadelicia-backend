package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Ítem del pedido en el request de creación")
public record OrderItemRequest(

  @Schema(description = "Id del producto", example = "1")
  @NotNull(message = "productId es obligatorio")
  Long productId,

  @Schema(description = "Id de la variante del producto", example = "10")
  @NotNull(message = "productVariantId es obligatorio")
  Long productVariantId,

  @Schema(
    description = "Cantidad de unidades para este ítem",
    example = "2",
    minimum = "1",
    maximum = "99"
  )
  @Min(value = 1, message = "quantity debe ser >= 1")
  @Max(value = 99, message = "quantity excede el máximo permitido (99)")
  int quantity
) {}
