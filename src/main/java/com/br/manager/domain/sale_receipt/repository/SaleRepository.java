package com.br.manager.domain.sale_receipt.repository;

import com.br.manager.domain.sale_receipt.entity.Sale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    boolean existsByStationIdAndSaleNumber(UUID stationId, String saleNumber);

    @EntityGraph(attributePaths = {"items", "payments", "refunds"})
    Optional<Sale> findDetailedById(UUID id);

    List<Sale> findByStationId(UUID stationId);
}
