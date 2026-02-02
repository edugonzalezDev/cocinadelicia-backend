package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ModifierOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierOptionRepository extends JpaRepository<ModifierOption, Long> {
  List<ModifierOption> findByModifierGroup_IdOrderBySortOrderAscIdAsc(Long groupId);
}
