package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Bomba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BombaRepository extends JpaRepository<Bomba, UUID> {
    @Query("SELECT b FROM Bomba b WHERE b.ativo = true")
    List<Bomba> findAllByAtivoTrue();

    @Query("SELECT b FROM Bomba b WHERE b.id = :id AND b.ativo = true")
    Bomba findByIdAndAtivoTrue(UUID id);

    @Query("SELECT b FROM Bomba b WHERE LOWER(b.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')) AND b.ativo = true")
    List<Bomba> findByCodeContainingIgnoreCaseAndAtivoTrue(String codigo);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT p FROM Bomba p WHERE (( p.atualizadoEm > :since OR p.excluidoEm > :since ) OR ( p.atualizadoEm IS NULL AND p.criadoEm > :since ))")
    List<Bomba> findAllChangedSince(@Param("since") LocalDateTime since);

}
