package com.br.manager.domain.organization.repository;

import com.br.manager.domain.organization.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findAllByActiveTrue();

    Company findByIdAndActiveTrue(UUID id);

    List<Company> findByLegalNameContainingIgnoreCaseAndActiveTrue(String legalName);
}
