package com.cocinadelicia.backend.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Estadísticas de órdenes para dashboard admin")
public record OrderStatsResponse(
    @Schema(description = "Total de órdenes activas") long totalActiveOrders,
    @Schema(description = "Órdenes en estado CREATED") long ordersCreated,
    @Schema(description = "Órdenes en estado CONFIRMED") long ordersConfirmed,
    @Schema(description = "Órdenes en estado PREPARING") long ordersPreparing,
    @Schema(description = "Órdenes en estado READY") long ordersReady,
    @Schema(description = "Órdenes en estado OUT_FOR_DELIVERY") long ordersOutForDelivery,
    @Schema(description = "Órdenes entregadas hoy") long ordersDeliveredToday,
    @Schema(description = "Órdenes canceladas hoy") long ordersCANCELLEDToday,
    @Schema(description = "Ingresos totales del día") BigDecimal totalRevenueToday,
    @Schema(description = "Ticket promedio del día") BigDecimal averageTicketToday) {}
