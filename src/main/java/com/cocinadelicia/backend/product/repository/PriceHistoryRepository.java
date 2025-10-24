package com.cocinadelicia.backend.product.repository;

import com.cocinadelicia.backend.product.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {}
