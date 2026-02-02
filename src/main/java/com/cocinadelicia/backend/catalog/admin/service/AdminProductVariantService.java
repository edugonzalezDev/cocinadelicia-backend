package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.product.dto.ProductVariantAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import com.cocinadelicia.backend.product.dto.ProductVariantPriceUpdateRequest;
import java.util.List;

public interface AdminProductVariantService {

  List<ProductVariantAdminResponse> getByProductId(Long productId);

  ProductVariantAdminResponse getById(Long id);

  ProductVariantAdminResponse create(Long productId, ProductVariantAdminRequest request);

  ProductVariantAdminResponse update(Long id, ProductVariantAdminRequest request);

  ProductVariantAdminResponse updateActivePrice(Long id, ProductVariantPriceUpdateRequest request);

  void delete(Long id);
}
