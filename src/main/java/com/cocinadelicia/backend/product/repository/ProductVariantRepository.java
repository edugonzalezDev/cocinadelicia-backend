package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {}
