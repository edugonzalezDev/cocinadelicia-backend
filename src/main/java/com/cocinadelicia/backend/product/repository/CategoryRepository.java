package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoryRepository extends JpaRepository<Category, Long> {}
