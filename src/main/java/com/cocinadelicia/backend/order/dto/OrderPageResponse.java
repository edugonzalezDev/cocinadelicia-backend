// src/main/java/com/cocinadelicia/backend/order/dto/OrderPageResponse.java
package com.cocinadelicia.backend.order.dto;

import java.util.List;

public record OrderPageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public static <T> OrderPageResponse<T> from(org.springframework.data.domain.Page<T> p) {
    return new OrderPageResponse<>(
        p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
  }
}
