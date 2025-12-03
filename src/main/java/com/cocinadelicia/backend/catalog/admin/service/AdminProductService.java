package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.product.dto.ProductAdminRequest;
import com.cocinadelicia.backend.product.dto.ProductAdminResponse;
import org.springframework.data.domain.Pageable;

public interface AdminProductService {

  PageResponse<ProductAdminResponse> getProducts(
      Long categoryId, Boolean isActive, Pageable pageable);

  ProductAdminResponse getById(Long id);

  ProductAdminResponse create(ProductAdminRequest request);

  ProductAdminResponse update(Long id, ProductAdminRequest request);

  void delete(Long id);
}
