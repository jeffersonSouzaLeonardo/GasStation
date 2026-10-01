package com.br.manager.domain.sale_receipt.repository;

import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRefundRepository extends JpaRepository<SaleRefund, UUID> {
    List<SaleRefund> findBySaleId(UUID saleId);
}
