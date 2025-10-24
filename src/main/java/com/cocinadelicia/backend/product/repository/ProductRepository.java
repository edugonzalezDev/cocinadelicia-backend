package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductRepository extends JpaRepository<Product, Long> {}
