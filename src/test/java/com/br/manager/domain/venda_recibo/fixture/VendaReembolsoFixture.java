package com.br.manager.domain.venda_recibo.fixture;

import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;

import java.time.Instant;
import java.util.UUID;

/**
 * Massa de dados padrao para estornos de venda.
 */
public final class VendaReembolsoFixture {

    public static final String REASON = "Cliente desistiu da compra";
    public static final String OBSERVACOES = "Estorno aprovado pelo gerente";
    public static final Instant REEMBOLSADO_EM = Instant.parse("2026-10-30T13:00:00Z");

    private VendaReembolsoFixture() {
    }

    public static VendaReembolsoInputDTO input(UUID vendaId, UUID requestedBy, UUID aprovadoPor) {
        VendaReembolsoInputDTO input = new VendaReembolsoInputDTO();
        input.setVendaId(vendaId);
        input.setMotivo(REASON);
        input.setRequestedBy(requestedBy);
        input.setAprovadoPor(aprovadoPor);
        input.setReembolsadoEm(REEMBOLSADO_EM);
        input.setObservacoes(OBSERVACOES);
        return input;
    }

    public static ReembolsoVenda persistedEntity(UUID requestedBy, UUID aprovadoPor) {
        ReembolsoVenda reembolso = new ReembolsoVenda();
        reembolso.setId(UUID.randomUUID());
        reembolso.setMotivo(REASON);
        reembolso.setRequestedBy(requestedBy);
        reembolso.setAprovadoPor(aprovadoPor);
        reembolso.setReembolsadoEm(REEMBOLSADO_EM);
        reembolso.setObservacoes(OBSERVACOES);
        return reembolso;
    }

    public static ReembolsoVenda persistedEntity() {
        return persistedEntity(UUID.randomUUID(), UUID.randomUUID());
    }
}
