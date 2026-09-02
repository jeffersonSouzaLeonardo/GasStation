package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.StationProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StationProductRepository extends JpaRepository<StationProduct, UUID> {
    List<StationProduct> findAllByActiveTrue();

    StationProduct findByIdAndActiveTrue(UUID id);

    List<StationProduct> findByStationIdAndActiveTrue(UUID stationId);

    List<StationProduct> findByProductIdAndActiveTrue(UUID productId);
}
