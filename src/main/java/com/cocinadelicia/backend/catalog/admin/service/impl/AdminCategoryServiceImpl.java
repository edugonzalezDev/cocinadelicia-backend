package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.service.AdminCategoryService;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.product.dto.CategoryAdminRequest;
import com.cocinadelicia.backend.product.dto.CategoryAdminResponse;
import com.cocinadelicia.backend.product.mapper.CategoryAdminMapper;
import com.cocinadelicia.backend.product.model.Category;
import com.cocinadelicia.backend.product.repository.CategoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final CategoryAdminMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<CategoryAdminResponse> getAll() {
    List<Category> list = categoryRepository.findAllByOrderByNameAsc();
    return list.stream().map(mapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryAdminResponse getById(Long id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException("CATEGORY_NOT_FOUND", "Categoría no encontrada: " + id));
    return mapper.toResponse(category);
  }

  @Override
  public CategoryAdminResponse create(CategoryAdminRequest request) {
    Category category = mapper.toNewEntity(request);
    Category saved = categoryRepository.save(category);
    log.info("AdminCategory.create id={} name={}", saved.getId(), saved.getName());
    return mapper.toResponse(saved);
  }

  @Override
  public CategoryAdminResponse update(Long id, CategoryAdminRequest request) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException("CATEGORY_NOT_FOUND", "Categoría no encontrada: " + id));

    mapper.updateEntityFromRequest(request, category);
    Category saved = categoryRepository.save(category);
    log.info("AdminCategory.update id={}", saved.getId());
    return mapper.toResponse(saved);
  }

  @Override
  public void delete(Long id) {
    // Opcional: validar que no haya productos asociados
    boolean hasProducts = productRepository.existsByCategory_Id(id);
    if (hasProducts) {
      throw new BadRequestException(
          "CATEGORY_IN_USE", "No se puede eliminar la categoría porque tiene productos asociados");
    }
    categoryRepository.deleteById(id);
    log.info("AdminCategory.delete id={}", id);
  }
}
