package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PumpRepository extends JpaRepository<Pump, UUID> {
    List<Pump> findAllByActiveTrue();

    Pump findByIdAndActiveTrue(UUID id);

    List<Pump> findByCodeContainingIgnoreCaseAndActiveTrue(String code);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT p FROM Pump p WHERE (( p.updatedAt > :since OR p.deletedAt > :since ) OR ( p.updatedAt IS NULL AND p.createdAt > :since ))")
    List<Pump> findAllChangedSince(@Param("since") LocalDateTime since);

}
