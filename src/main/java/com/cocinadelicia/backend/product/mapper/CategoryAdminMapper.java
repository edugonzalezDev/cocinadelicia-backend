package com.cocinadelicia.backend.product.mapper;

import com.cocinadelicia.backend.product.dto.CategoryAdminRequest;
import com.cocinadelicia.backend.product.dto.CategoryAdminResponse;
import com.cocinadelicia.backend.product.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryAdminMapper {

  public CategoryAdminResponse toResponse(Category entity) {
    if (entity == null) return null;
    return new CategoryAdminResponse(
        entity.getId(), entity.getName(), entity.getSlug(), entity.getDescription());
  }

  public void updateEntityFromRequest(CategoryAdminRequest req, Category entity) {
    if (req.name() != null) entity.setName(req.name());
    if (req.slug() != null) entity.setSlug(req.slug());
    if (req.description() != null) entity.setDescription(req.description());
  }

  public Category toNewEntity(CategoryAdminRequest req) {
    Category category = new Category();
    category.setName(req.name());
    category.setSlug(req.slug());
    category.setDescription(req.description());
    return category;
  }
}
