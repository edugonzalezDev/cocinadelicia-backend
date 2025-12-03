// src/main/java/com/cocinadelicia/backend/product/repository/ProductRepository.java
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
}
