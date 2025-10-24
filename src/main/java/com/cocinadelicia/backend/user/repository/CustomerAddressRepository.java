package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {}
