package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.product.dto.ModifierGroupAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminResponse;
import java.util.List;

public interface AdminModifierGroupService {
  List<ModifierGroupAdminResponse> getByVariant(Long productVariantId);

  ModifierGroupAdminResponse getById(Long id);

  ModifierGroupAdminResponse create(ModifierGroupAdminRequest request);

  ModifierGroupAdminResponse update(Long id, ModifierGroupAdminRequest request);

  void delete(Long id);

  List<ModifierGroupAdminResponse> getByProduct(Long productId);

  ModifierGroupAdminResponse createForProduct(Long productId, ModifierGroupAdminRequest request);
}
