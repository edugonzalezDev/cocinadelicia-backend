// src/main/java/com/cocinadelicia/backend/catalog/controller/CatalogController.java
package com.cocinadelicia.backend.catalog.controller;

import com.cocinadelicia.backend.catalog.dto.CatalogFilter;
import com.cocinadelicia.backend.catalog.dto.CategorySummaryResponse;
import com.cocinadelicia.backend.catalog.dto.ProductSummaryResponse;
import com.cocinadelicia.backend.catalog.service.CatalogService;
import com.cocinadelicia.backend.common.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "catalog", description = "Catálogo público de productos")
public class CatalogController {

  private final CatalogService catalogService;

  @Operation(
      summary = "Listar categorías activas del catálogo",
      description =
          """
                  Devuelve la lista de categorías activas del catálogo, ordenadas por nombre.
                  Endpoint público: no requiere autenticación.
                  """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de categorías activas",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategorySummaryResponse.class)))
      })
  @GetMapping("/categories")
  public ResponseEntity<List<CategorySummaryResponse>> getCategories() {
    return ResponseEntity.ok(catalogService.getCategories());
  }

  @Operation(
      summary = "Listar productos del catálogo",
      description =
          """
      Lista paginada de productos activos del catálogo.

      Filtros soportados:
      - q (opcional): texto de búsqueda en nombre, descripción y tags.
      - categorySlug (opcional): filtra por categoría.
      - availableOnly (opcional): si es true, solo productos con variantes disponibles.
      - featured (opcional): si es true, solo productos con variantes destacadas.
      - dailyMenu (opcional): si es true, solo productos marcados como menú del día.
      - new (opcional): si es true, solo productos nuevos.
      - page / size: paginación estándar (0-based).
      """,
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Página de productos del catálogo",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PageResponse.class)))
      })
  @GetMapping("/products")
  public ResponseEntity<PageResponse<ProductSummaryResponse>> getProducts(
      @Parameter(description = "Texto de búsqueda (busca en nombre, descripción y tags)", example = "milanesa")
          @RequestParam(name = "q", required = false)
          String searchQuery,
      @Parameter(description = "Slug de la categoría a filtrar (opcional)", example = "empanadas")
          @RequestParam(name = "categorySlug", required = false)
          String categorySlug,
      @Parameter(
              description = "Si es true, solo productos con variantes disponibles",
              example = "true")
          @RequestParam(name = "availableOnly", required = false)
          Boolean availableOnly,
      @Parameter(
              description = "Si es true, solo productos con variantes destacadas",
              example = "true")
          @RequestParam(name = "featured", required = false)
          Boolean featured,
      @Parameter(
              description = "Si es true, solo productos marcados como menú del día",
              example = "true")
          @RequestParam(name = "dailyMenu", required = false)
          Boolean dailyMenu,
      @Parameter(name = "new", description = "Si es true, solo productos nuevos", example = "true")
          @RequestParam(name = "new", required = false)
          Boolean isNew,
      @ParameterObject @PageableDefault(size = 12) Pageable pageable) {

    var filter =
        new CatalogFilter(
            searchQuery,
            categorySlug,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort(),
            featured,
            dailyMenu,
            isNew,
            availableOnly);

    return ResponseEntity.ok(catalogService.getProducts(filter));
  }
}
