package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VendaResponseDTO(
        UUID id,
        UUID estacaoId,
        UUID turnoId,
        String numeroVenda,
        VendaSituacao status,
        Instant vendidoEm,
        UUID clienteId,
        UUID veiculoId,
        UUID frentistaUsuarioId,
        UUID caixeiroUsuarioId,
        BigDecimal subtotal,
        BigDecimal valorDesconto,
        SituacaoFiscal situacaoFiscal,
        String observacoes,
        List<VendaItemResponseDTO> itens,
        List<VendaPagamentoResponseDTO> pagamentos,
        List<VendaReembolsoResponseDTO> reembolsos
) {
}
