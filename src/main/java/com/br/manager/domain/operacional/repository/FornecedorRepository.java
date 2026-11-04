package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, UUID> {
    @Query("SELECT f FROM Fornecedor f WHERE f.ativo = true")
    List<Fornecedor> findAllByAtivoTrue();

    @Query("SELECT f FROM Fornecedor f WHERE f.id = :id AND f.ativo = true")
    Fornecedor findByIdAndAtivoTrue(UUID id);

    @Query("SELECT f FROM Fornecedor f WHERE LOWER(f.legalName) LIKE LOWER(CONCAT('%', :legalName, '%')) AND f.ativo = true")
    List<Fornecedor> findByLegalNameContainingIgnoreCaseAndAtivoTrue(String legalName);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT s FROM Fornecedor s WHERE (( s.atualizadoEm > :since OR s.excluidoEm > :since ) OR ( s.atualizadoEm IS NULL AND s.criadoEm > :since ))")
    List<Fornecedor> findAllChangedSince(@Param("since") LocalDateTime since);

}
