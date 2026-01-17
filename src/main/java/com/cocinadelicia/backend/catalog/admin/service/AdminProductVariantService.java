package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.product.dto.ProductVariantAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import java.util.List;

public interface AdminProductVariantService {

  List<ProductVariantAdminResponse> getByProductId(Long productId);

  ProductVariantAdminResponse getById(Long id);

  ProductVariantAdminResponse create(Long productId, ProductVariantAdminRequest request);

  ProductVariantAdminResponse update(Long id, ProductVariantAdminRequest request);

  void delete(Long id);
}
