package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ModifierGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierGroupRepository extends JpaRepository<ModifierGroup, Long> {

  List<ModifierGroup> findByProductVariant_IdAndActiveTrueOrderBySortOrderAscIdAsc(Long variantId);

  List<ModifierGroup> findByProductVariant_IdOrderBySortOrderAscIdAsc(Long variantId);

  @EntityGraph(attributePaths = {"options", "options.linkedProductVariant"})
  @Query("select g from ModifierGroup g where g.id = :id")
  Optional<ModifierGroup> findByIdWithOptions(@Param("id") Long id);

  List<ModifierGroup> findByProductVariant_Product_IdOrderBySortOrderAscIdAsc(Long productId);
}
