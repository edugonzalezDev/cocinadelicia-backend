package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ProductImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

  /**
   * Lista de imágenes activas (no borradas) de un producto,
   * ordenadas por sortOrder (y luego id por @OrderBy de la entidad).
   */
  List<ProductImage> findByProduct_IdOrderBySortOrderAsc(Long productId);

  /**
   * Imagen principal (isMain = true) del producto, si existe.
   */
  Optional<ProductImage> findFirstByProduct_IdAndIsMainTrue(Long productId);
}
