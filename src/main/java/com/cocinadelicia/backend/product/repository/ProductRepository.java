// src/main/java/com/cocinadelicia/backend/product/repository/ProductRepository.java
package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Lista productos activos (isActive = true), no eliminados (por @Where).
   */
  Page<Product> findByIsActiveTrue(Pageable pageable);

  /**
   * Lista productos activos filtrando por slug de categoría (case-insensitive).
   */
  Page<Product> findByIsActiveTrueAndCategory_SlugIgnoreCase(String categorySlug, Pageable pageable);
}
