// src/main/java/com/cocinadelicia/backend/order/dto/OrderFilter.java
package com.cocinadelicia.backend.order.dto;

import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import java.time.LocalDate;
import java.util.List;

public record OrderFilter(
    List<OrderStatus> statuses, // null o vacío => sin filtro
    LocalDate from, // inclusive
    LocalDate to, // inclusive
    Long userId // opcional (para futuros filtros por usuario)
    ) {}
