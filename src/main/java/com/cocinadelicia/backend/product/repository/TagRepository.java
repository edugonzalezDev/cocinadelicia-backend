package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {}
