package com.br.manager.domain.organizacao.repository;

import com.br.manager.domain.organizacao.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    @Query("SELECT e FROM Empresa e WHERE e.ativo = true")
    List<Empresa> findAllByAtivoTrue();

    @Query("SELECT e FROM Empresa e WHERE e.id = :id AND e.ativo = true")
    Empresa findByIdAndAtivoTrue(UUID id);

    @Query("SELECT e FROM Empresa e WHERE LOWER(e.legalName) LIKE LOWER(CONCAT('%', :legalName, '%')) AND e.ativo = true")
    List<Empresa> findByLegalNameContainingIgnoreCaseAndAtivoTrue(String legalName);
}
