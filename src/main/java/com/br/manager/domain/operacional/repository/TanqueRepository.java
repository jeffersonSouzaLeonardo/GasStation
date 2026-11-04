package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Tanque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TanqueRepository extends JpaRepository<Tanque, UUID> {
    @Query("SELECT t FROM Tanque t WHERE t.ativo = true")
    List<Tanque> findAllByAtivoTrue();
    @Query("SELECT t FROM Tanque t WHERE t.id = :id AND t.ativo = true")
    Tanque findByIdAndAtivoTrue(UUID id);
    @Query("SELECT t FROM Tanque t WHERE LOWER(t.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')) AND t.ativo = true")
    List<Tanque> findByCodeContainingIgnoreCaseAndAtivoTrue(String codigo);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT t FROM Tanque t WHERE (( t.atualizadoEm > :since OR t.excluidoEm > :since ) OR ( t.atualizadoEm IS NULL AND t.criadoEm > :since ))")
    List<Tanque> findAllChangedSince(@Param("since") LocalDateTime since);
}
