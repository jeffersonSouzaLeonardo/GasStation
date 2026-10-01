package com.br.manager.domain.price_acquisition.repository;

import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {
    List<GoodsReceipt> findBySupplierId(UUID supplierId);
    List<GoodsReceipt> findByPurchaseOrderId(UUID purchaseOrderId);
}
