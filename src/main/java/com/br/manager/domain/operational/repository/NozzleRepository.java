package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Nozzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NozzleRepository extends JpaRepository<Nozzle, UUID> {
    List<Nozzle> findAllByActiveTrue();

    Nozzle findByIdAndActiveTrue(UUID id);

    List<Nozzle> findByCodeContainingIgnoreCaseAndActiveTrue(String code);

    List<Nozzle> findByPumpIdAndActiveTrue(UUID pumpId);

    List<Nozzle> findByTankIdAndActiveTrue(UUID tankId);
}
