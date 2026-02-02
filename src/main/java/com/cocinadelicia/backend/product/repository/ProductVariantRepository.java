package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ProductVariant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

  @Query(
      value = "select * from product_variant where id = :id and deleted_at is null for update",
      nativeQuery = true)
  Optional<ProductVariant> findByIdForUpdate(@Param("id") Long id);
}
