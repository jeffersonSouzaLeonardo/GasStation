package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Bico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BicoRepository extends JpaRepository<Bico, UUID> {
    @Query("SELECT b FROM Bico b WHERE b.ativo = true")
    List<Bico> findAllByAtivoTrue();

    @Query("SELECT b FROM Bico b WHERE b.id = :id AND b.ativo = true")
    Bico findByIdAndAtivoTrue(UUID id);

    @Query("SELECT b FROM Bico b WHERE LOWER(b.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')) AND b.ativo = true")
    List<Bico> findByCodeContainingIgnoreCaseAndAtivoTrue(String codigo);

    @Query("SELECT b FROM Bico b WHERE b.bombaId = :bombaId AND b.ativo = true")
    List<Bico> findByBombaIdAndAtivoTrue(UUID bombaId);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT n FROM Bico n WHERE (( n.atualizadoEm > :since OR n.excluidoEm > :since ) OR ( n.atualizadoEm IS NULL AND n.criadoEm > :since ))")
    List<Bico> findAllChangedSince(@Param("since") LocalDateTime since);

}
