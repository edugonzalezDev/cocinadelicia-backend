package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Respuesta paginada genérica de pedidos")
public record OrderPageResponse<T>(
    @Schema(description = "Contenido de la página") List<T> content,
    @Schema(description = "Número de página (0-based)", example = "0") int page,
    @Schema(description = "Tamaño de página", example = "20") int size,
    @Schema(description = "Cantidad total de elementos", example = "125") long totalElements,
    @Schema(description = "Cantidad total de páginas", example = "7") int totalPages) {

  public static <T> OrderPageResponse<T> from(org.springframework.data.domain.Page<T> p) {
    return new OrderPageResponse<>(
        p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
  }
}
