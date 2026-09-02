package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Tank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TankRepository extends JpaRepository<Tank, UUID> {
    List<Tank> findAllByActiveTrue();
    Tank findByIdAndActiveTrue(UUID id);
    List<Tank> findByCodeContainingIgnoreCaseAndActiveTrue(String code);
    List<Tank> findByStationIdAndActiveTrue(UUID stationId);
    List<Tank> findByProductIdAndActiveTrue(UUID productId);
}
