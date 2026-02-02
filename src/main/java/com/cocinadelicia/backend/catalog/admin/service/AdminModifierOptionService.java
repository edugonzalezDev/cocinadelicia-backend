package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.product.dto.ModifierOptionAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminResponse;
import java.util.List;

public interface AdminModifierOptionService {
  List<ModifierOptionAdminResponse> getByGroup(Long groupId);

  ModifierOptionAdminResponse getById(Long id);

  ModifierOptionAdminResponse create(Long groupId, ModifierOptionAdminRequest request);

  ModifierOptionAdminResponse update(Long id, ModifierOptionAdminRequest request);

  void delete(Long id);
}
