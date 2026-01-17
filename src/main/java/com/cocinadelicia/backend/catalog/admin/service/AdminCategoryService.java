package com.cocinadelicia.backend.catalog.admin.service;

import com.cocinadelicia.backend.product.dto.CategoryAdminRequest;
import com.cocinadelicia.backend.product.dto.CategoryAdminResponse;
import java.util.List;

public interface AdminCategoryService {

  List<CategoryAdminResponse> getAll();

  CategoryAdminResponse getById(Long id);

  CategoryAdminResponse create(CategoryAdminRequest request);

  CategoryAdminResponse update(Long id, CategoryAdminRequest request);

  void delete(Long id);
}
