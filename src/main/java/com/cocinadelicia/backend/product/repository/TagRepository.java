package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

interface TagRepository extends JpaRepository<Tag, Long> {}
