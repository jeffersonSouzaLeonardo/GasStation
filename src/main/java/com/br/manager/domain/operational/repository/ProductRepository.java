package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByActiveTrue();

    Product findByIdAndActiveTrue(UUID id);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Product> findBySkuContainingIgnoreCaseAndActiveTrue(String sku);

    List<Product> findByCompanyIdAndActiveTrue(UUID companyId);
}
