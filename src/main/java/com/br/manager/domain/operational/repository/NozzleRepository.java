package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Nozzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NozzleRepository extends JpaRepository<Nozzle, UUID> {
    List<Nozzle> findAllByActiveTrue();

    Nozzle findByIdAndActiveTrue(UUID id);

    List<Nozzle> findByCodeContainingIgnoreCaseAndActiveTrue(String code);

    List<Nozzle> findByPumpIdAndActiveTrue(UUID pumpId);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT n FROM Nozzle n WHERE (( n.updatedAt > :since OR n.deletedAt > :since ) OR ( n.updatedAt IS NULL AND n.createdAt > :since ))")
    List<Nozzle> findAllChangedSince(@Param("since") LocalDateTime since);

}
