package com.br.manager.domain.venda_recibo.fixture;

import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Massa de dados padrao para pagamentos de venda.
 */
public final class VendaPagamentoFixture {

    public static final BigDecimal VALOR = new BigDecimal("100.0000");
    public static final String REFERENCIA_TRANSACAO = "NSU-123456";
    public static final Instant AUTORIZADO_EM = Instant.parse("2026-10-30T12:05:00Z");
    public static final Instant RECEBIDO_EM = Instant.parse("2026-10-30T12:06:00Z");

    private VendaPagamentoFixture() {
    }

    public static VendaPagamentoInputDTO input(UUID vendaId, UUID pagamentoMethodId) {
        VendaPagamentoInputDTO input = new VendaPagamentoInputDTO();
        input.setVendaId(vendaId);
        input.setPagamentoMethodId(pagamentoMethodId);
        input.setValor(VALOR);
        input.setStatus(SituacaoPagamento.RECEBIDO);
        input.setReferenciaTransacao(REFERENCIA_TRANSACAO);
        input.setAutorizadoEm(AUTORIZADO_EM);
        input.setRecebidoEm(RECEBIDO_EM);
        return input;
    }

    public static PagamentoVenda persistedEntity(UUID pagamentoMethodId, SituacaoPagamento status) {
        PagamentoVenda pagamento = new PagamentoVenda();
        pagamento.setId(UUID.randomUUID());
        pagamento.setPagamentoMethodId(pagamentoMethodId);
        pagamento.setValor(VALOR);
        pagamento.setStatus(status);
        pagamento.setReferenciaTransacao(REFERENCIA_TRANSACAO);
        pagamento.setAutorizadoEm(AUTORIZADO_EM);
        pagamento.setRecebidoEm(RECEBIDO_EM);
        return pagamento;
    }

    public static PagamentoVenda persistedEntity(SituacaoPagamento status) {
        return persistedEntity(UUID.randomUUID(), status);
    }
}
