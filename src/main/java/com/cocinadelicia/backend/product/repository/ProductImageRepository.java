package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ProductImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

  // ✅ Orden: sortOrder ASC y luego createdAt ASC (si tu BaseAudit no tiene createdAt, ver preguntas
  // al final)
  List<ProductImage> findByProduct_IdOrderBySortOrderAscCreatedAtAsc(Long productId);

  Optional<ProductImage> findFirstByProduct_IdAndIsMainTrue(Long productId);

  boolean existsByProduct_IdAndIsMainTrue(Long productId);
}
