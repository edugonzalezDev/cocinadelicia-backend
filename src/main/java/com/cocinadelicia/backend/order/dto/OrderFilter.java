package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Filtro interno para búsqueda de pedidos")
public record OrderFilter(
    @Schema(
            description = "Estados del pedido a filtrar. Null o vacío => sin filtro.",
            example = "[\"CREATED\", \"PREPARING\"]")
        List<OrderStatus> statuses, // null o vacío => sin filtro
    @Schema(description = "Fecha desde (inclusive)", example = "2025-11-01")
        LocalDate from, // inclusive
    @Schema(description = "Fecha hasta (inclusive)", example = "2025-11-30")
        LocalDate to, // inclusive
    @Schema(description = "Id de usuario (para futuros filtros por usuario)", example = "123")
        Long userId // opcional (para futuros filtros por usuario)
    ) {}
