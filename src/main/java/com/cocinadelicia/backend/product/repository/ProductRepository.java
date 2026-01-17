package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  Page<Product> findByIsActiveTrue(Pageable pageable);

  Page<Product> findByIsActiveTrueAndCategory_SlugIgnoreCase(String slug, Pageable pageable);

  Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

  Page<Product> findByIsActive(boolean isActive, Pageable pageable);

  Page<Product> findByCategory_IdAndIsActive(Long categoryId, boolean isActive, Pageable pageable);

  boolean existsByCategory_Id(Long categoryId);
}
