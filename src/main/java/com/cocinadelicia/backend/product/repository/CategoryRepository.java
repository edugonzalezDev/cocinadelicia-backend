// src/main/java/com/cocinadelicia/backend/product/repository/CategoryRepository.java
package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * Devuelve todas las categorías NO eliminadas (gracias a @Where) ordenadas por nombre ascendente.
   */
  List<Category> findAllByOrderByNameAsc();

  /** Útil si más adelante querés validar que exista una categoría por slug. */
  boolean existsBySlugIgnoreCase(String slug);
}
