package com.br.manager.domain.price_acquisition.repository;

import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByNumber(String number);

    List<PurchaseOrder> findByStationId(UUID stationId);

    List<PurchaseOrder> findBySupplierId(UUID supplierId);
}
