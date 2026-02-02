package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.FulfillmentType;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Schema(description = "Filtros para búsqueda avanzada de órdenes (Admin)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFilterRequest {

  @Schema(description = "Filtrar por estado", example = "PREPARING")
  private OrderStatus status;

  @Schema(description = "Fecha de creación desde (inclusive)", example = "2026-01-01")
  private LocalDate createdAfter;

  @Schema(description = "Fecha de creación hasta (inclusive)", example = "2026-01-31")
  private LocalDate createdBefore;

  @Schema(description = "Filtrar por email de usuario", example = "cliente@example.com")
  private String userEmail;

  @Schema(description = "Filtrar por email de chef asignado", example = "chef@example.com")
  private String assignedChefEmail;

  @Schema(description = "Filtrar por tipo de entrega", example = "DELIVERY")
  private FulfillmentType fulfillment;

  @Schema(
      description = "Texto libre para buscar por id, notas o datos de envío",
      example = "gluten")
  @Size(max = 200) private String q;

  @Schema(description = "Fecha solicitada desde (inclusive)", example = "2026-02-01")
  private LocalDate requestedAfter;

  @Schema(description = "Fecha solicitada hasta (inclusive)", example = "2026-02-10")
  private LocalDate requestedBefore;

  @Schema(description = "Filtrar por id de producto", example = "1001")
  private Long productId;

  @Schema(description = "Filtrar por id de variante", example = "2001")
  private Long productVariantId;

  @Schema(description = "Monto mínimo", example = "100.00")
  private BigDecimal minAmount;

  @Schema(description = "Monto máximo", example = "5000.00")
  private BigDecimal maxAmount;

  @Schema(description = "Incluir órdenes eliminadas (soft delete)", example = "false")
  @Builder.Default
  private Boolean includeDeleted = false;
}
