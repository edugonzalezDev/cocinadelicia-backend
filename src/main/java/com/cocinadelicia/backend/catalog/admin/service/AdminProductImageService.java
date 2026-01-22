package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminPatchRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminResponse;
import java.util.List;

public interface AdminProductImageService {

  List<ProductImageAdminResponse> listByProduct(long productId);

  ProductImageAdminResponse addToProduct(long productId, ProductImageAdminRequest req);

  ProductImageAdminResponse patch(long imageId, ProductImageAdminPatchRequest req);

  void delete(long imageId);
}
