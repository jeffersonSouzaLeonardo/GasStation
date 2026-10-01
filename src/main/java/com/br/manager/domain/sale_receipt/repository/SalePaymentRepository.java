package com.br.manager.domain.sale_receipt.repository;

import com.br.manager.domain.sale_receipt.entity.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalePaymentRepository extends JpaRepository<SalePayment, UUID> {
    List<SalePayment> findBySaleId(UUID saleId);
}
