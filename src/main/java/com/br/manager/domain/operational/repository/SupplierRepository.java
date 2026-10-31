package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findAllByActiveTrue();

    Supplier findByIdAndActiveTrue(UUID id);

    List<Supplier> findByLegalNameContainingIgnoreCaseAndActiveTrue(String legalName);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT s FROM Supplier s WHERE (( s.updatedAt > :since OR s.deletedAt > :since ) OR ( s.updatedAt IS NULL AND s.createdAt > :since ))")
    List<Supplier> findAllChangedSince(@Param("since") LocalDateTime since);

}
