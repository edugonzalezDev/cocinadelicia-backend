package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.order.dto.OrderItemModifierRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request para actualizar cantidades o eliminar ítems de una orden (Admin)")
public record UpdateOrderItemsRequest(
    @Schema(description = "Lista de cambios por ítem", required = true)
        @NotEmpty(message = "Debe incluir al menos un ítem") @Valid List<OrderItemUpdate> items) {

  public record OrderItemUpdate(
      @Schema(description = "Id del ítem existente (opcional)", example = "101") Long orderItemId,
      @Schema(description = "Id del producto (para agregar ítem nuevo)", example = "10")
          Long productId,
      @Schema(description = "Id de la variante (para agregar ítem nuevo)", example = "100")
          Long productVariantId,
      @Schema(
              description = "Cantidad nueva. Si es 0 se elimina el ítem. Debe ser >= 0.",
              example = "2")
          @Min(value = 0, message = "quantity debe ser >= 0") int quantity,
      @Schema(description = "Modificadores seleccionados para este ítem") @Valid List<OrderItemModifierRequest> modifiers) {}
}
