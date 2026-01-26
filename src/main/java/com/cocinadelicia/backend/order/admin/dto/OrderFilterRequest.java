package com.cocinadelicia.backend.order.admin.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "Monto mínimo", example = "100.00")
  private BigDecimal minAmount;

  @Schema(description = "Monto máximo", example = "5000.00")
  private BigDecimal maxAmount;

  @Schema(description = "Incluir órdenes eliminadas (soft delete)", example = "false")
  @Builder.Default
  private Boolean includeDeleted = false;
}
