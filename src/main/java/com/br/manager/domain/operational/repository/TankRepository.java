package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Tank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TankRepository extends JpaRepository<Tank, UUID> {
    List<Tank> findAllByActiveTrue();
    Tank findByIdAndActiveTrue(UUID id);
    List<Tank> findByCodeContainingIgnoreCaseAndActiveTrue(String code);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT t FROM Tank t WHERE (( t.updatedAt > :since OR t.deletedAt > :since ) OR ( t.updatedAt IS NULL AND t.createdAt > :since ))")
    List<Tank> findAllChangedSince(@Param("since") LocalDateTime since);
}
