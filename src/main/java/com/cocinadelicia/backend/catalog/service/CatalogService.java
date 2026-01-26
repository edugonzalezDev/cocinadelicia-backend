// src/main/java/com/cocinadelicia/backend/catalog/service/CatalogService.java
package com.cocinadelicia.backend.catalog.service;

import com.cocinadelicia.backend.catalog.dto.CatalogFilter;
import com.cocinadelicia.backend.catalog.dto.CategorySummaryResponse;
import com.cocinadelicia.backend.catalog.dto.ProductDetailResponse;
import com.cocinadelicia.backend.catalog.dto.ProductSummaryResponse;
import com.cocinadelicia.backend.common.web.PageResponse;
import java.util.List;

public interface CatalogService {

  /** Devuelve las categorías activas/vigentes para el catálogo público. */
  List<CategorySummaryResponse> getCategories();

  /** Devuelve una página de productos activos, filtrados opcionalmente por categoría. */
  PageResponse<ProductSummaryResponse> getProducts(CatalogFilter filter);

  /** Obtiene el detalle público de un producto por slug. */
  ProductDetailResponse getProductBySlug(String slug);
}
