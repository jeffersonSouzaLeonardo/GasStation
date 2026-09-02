package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByActiveTrue();

    Customer findByIdAndActiveTrue(UUID id);

    List<Customer> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Customer> findByCompanyIdAndActiveTrue(UUID companyId);
}
