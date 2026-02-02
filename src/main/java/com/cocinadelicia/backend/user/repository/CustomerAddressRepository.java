package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.CustomerAddress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
  List<CustomerAddress> findByUser_Id(Long userId);
}
