package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findAllByActiveTrue();

    Supplier findByIdAndActiveTrue(UUID id);

    List<Supplier> findByLegalNameContainingIgnoreCaseAndActiveTrue(String legalName);

    List<Supplier> findByCompanyIdAndActiveTrue(UUID companyId);
}
