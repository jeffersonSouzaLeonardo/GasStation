package com.br.manager.domain.venda_recibo.fixture;

import com.br.manager.domain.venda_recibo.dto.VendaInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para vendas.
 */
public final class VendaFixture {

    public static final String NUMERO_VENDA = "VEN-0001";
    public static final String OUTROS_VENDA_NUMERO = "VEN-0002";
    public static final Instant VENDIDO_EM = Instant.parse("2026-10-30T12:00:00Z");
    public static final BigDecimal SUBTOTAL = new BigDecimal("100.0000");
    public static final BigDecimal DESCONTO = new BigDecimal("2.5000");
    public static final String OBSERVACOES = "Venda no balcao";
    public static final String OBSERVACOES_ATUALIZADAS = "Venda revisada pelo caixa";
    public static final SituacaoFiscal FISCAL_SITUACAO = SituacaoFiscal.EMISSAO_PENDENTE;

    private VendaFixture() {
    }

    public static VendaInputDTO input(UUID estacaoId, UUID turnoId, UUID caixeiroUsuarioId, List<VendaItemInputDTO> itens) {
        VendaInputDTO input = new VendaInputDTO();
        input.setEstacaoId(estacaoId);
        input.setTurnoId(turnoId);
        input.setCaixeiroUsuarioId(caixeiroUsuarioId);
        input.setNumeroVenda(NUMERO_VENDA);
        input.setVendidoEm(VENDIDO_EM);
        input.setValorDesconto(DESCONTO);
        input.setSituacaoFiscal(FISCAL_SITUACAO);
        input.setObservacoes(OBSERVACOES);
        input.setItens(itens);
        return input;
    }

    public static VendaInputDTO updateInput(UUID vendaId) {
        VendaInputDTO input = new VendaInputDTO();
        input.setId(vendaId);
        input.setObservacoes(OBSERVACOES_ATUALIZADAS);
        return input;
    }

    public static Venda entity(UUID estacaoId, UUID turnoId, UUID caixeiroUsuarioId, VendaSituacao status) {
        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setEstacaoId(estacaoId);
        venda.setTurnoId(turnoId);
        venda.setCaixeiroUsuarioId(caixeiroUsuarioId);
        venda.setNumeroVenda(NUMERO_VENDA);
        venda.setStatus(status);
        venda.setVendidoEm(VENDIDO_EM);
        venda.setSubtotal(SUBTOTAL);
        venda.setValorDesconto(DESCONTO);
        venda.setSituacaoFiscal(FISCAL_SITUACAO);
        venda.setObservacoes(OBSERVACOES);
        return venda;
    }

    public static Venda persistedEntity(VendaSituacao status) {
        return entity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), status);
    }
}
