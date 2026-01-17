// src/main/java/com/cocinadelicia/backend/common/web/PageResponse.java
package com.cocinadelicia.backend.common.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "Respuesta paginada genérica")
public record PageResponse<T>(
    @Schema(description = "Contenido de la página") List<T> content,
    @Schema(description = "Número de página (0-based)", example = "0") int page,
    @Schema(description = "Tamaño de página", example = "20") int size,
    @Schema(description = "Cantidad total de elementos", example = "125") long totalElements,
    @Schema(description = "Cantidad total de páginas", example = "7") int totalPages) {

  public static <T> PageResponse<T> from(Page<T> p) {
    return new PageResponse<>(
        p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
  }
}
