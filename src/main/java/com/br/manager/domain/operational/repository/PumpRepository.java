package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PumpRepository extends JpaRepository<Pump, UUID> {
    List<Pump> findAllByActiveTrue();

    Pump findByIdAndActiveTrue(UUID id);

    List<Pump> findByCodeContainingIgnoreCaseAndActiveTrue(String code);

    List<Pump> findByStationIdAndActiveTrue(UUID stationId);
}
