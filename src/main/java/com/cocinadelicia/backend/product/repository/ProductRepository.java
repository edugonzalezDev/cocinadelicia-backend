package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  Page<Product> findByIsActiveTrue(Pageable pageable);

  Page<Product> findByIsActiveTrueAndCategory_SlugIgnoreCase(String slug, Pageable pageable);

  Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

  Page<Product> findByIsActive(boolean isActive, Pageable pageable);

  Page<Product> findByCategory_IdAndIsActive(Long categoryId, boolean isActive, Pageable pageable);

  boolean existsByCategory_Id(Long categoryId);

  /**
   * Query optimizada con fetch joins para evitar N+1 queries en catálogo.
   * Carga productos con sus variantes y precios actuales en una sola consulta.
   */
  @Query(
      """
      SELECT DISTINCT p FROM Product p
      LEFT JOIN FETCH p.variants v
      LEFT JOIN FETCH p.category
      WHERE p.deletedAt IS NULL
      AND p.isActive = true
      ORDER BY p.name
      """)
  List<Product> findAllActiveWithVariantsAndCategory();

  /**
   * Obtiene IDs de productos activos para paginación manual optimizada.
   */
  @Query(
      """
      SELECT p.id FROM Product p
      WHERE p.deletedAt IS NULL
      AND p.isActive = true
      ORDER BY p.name
      """)
  List<Long> findAllActiveProductIds(Pageable pageable);

  /**
   * Carga productos por IDs con fetch joins (segunda query de paginación optimizada).
   */
  @Query(
      """
      SELECT DISTINCT p FROM Product p
      LEFT JOIN FETCH p.variants v
      LEFT JOIN FETCH p.category
      LEFT JOIN FETCH p.images
      LEFT JOIN FETCH p.tags
      WHERE p.id IN :ids
      ORDER BY p.name
      """)
  List<Product> findByIdsWithFetchJoins(@Param("ids") List<Long> ids);

  @EntityGraph(attributePaths = {"category", "variants", "tags"})
  Optional<Product> findBySlugIgnoreCaseAndIsActiveTrue(String slug);
}
