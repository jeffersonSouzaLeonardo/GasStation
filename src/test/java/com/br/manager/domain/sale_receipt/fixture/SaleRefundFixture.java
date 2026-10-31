package com.br.manager.domain.sale_receipt.fixture;

import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;

import java.time.Instant;
import java.util.UUID;

/**
 * Massa de dados padrao para estornos de venda.
 */
public final class SaleRefundFixture {

    public static final String REASON = "Cliente desistiu da compra";
    public static final String NOTES = "Estorno aprovado pelo gerente";
    public static final Instant REFUNDED_AT = Instant.parse("2026-10-30T13:00:00Z");

    private SaleRefundFixture() {
    }

    public static SaleRefundInputDTO input(UUID saleId, UUID requestedBy, UUID approvedBy) {
        SaleRefundInputDTO input = new SaleRefundInputDTO();
        input.setSaleId(saleId);
        input.setReason(REASON);
        input.setRequestedBy(requestedBy);
        input.setApprovedBy(approvedBy);
        input.setRefundedAt(REFUNDED_AT);
        input.setNotes(NOTES);
        return input;
    }

    public static SaleRefund persistedEntity(UUID requestedBy, UUID approvedBy) {
        SaleRefund refund = new SaleRefund();
        refund.setId(UUID.randomUUID());
        refund.setReason(REASON);
        refund.setRequestedBy(requestedBy);
        refund.setApprovedBy(approvedBy);
        refund.setRefundedAt(REFUNDED_AT);
        refund.setNotes(NOTES);
        return refund;
    }

    public static SaleRefund persistedEntity() {
        return persistedEntity(UUID.randomUUID(), UUID.randomUUID());
    }
}
