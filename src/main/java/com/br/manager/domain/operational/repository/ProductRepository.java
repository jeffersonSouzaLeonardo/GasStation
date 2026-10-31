package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByActiveTrue();

    Product findByIdAndActiveTrue(UUID id);

    List<Product> findByProductTypeAndActiveTrue(ProductTypeEnum type);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT p FROM Product p WHERE (( p.updatedAt > :since OR p.deletedAt > :since ) OR ( p.updatedAt IS NULL AND p.createdAt > :since ))")
    List<Product> findAllChangedSince(@Param("since") LocalDateTime since);

}
