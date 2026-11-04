package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VendaPagamentoResponseDTO(
        UUID id,
        UUID pagamentoMethodId,
        BigDecimal valor,
        SituacaoPagamento status,
        String referenciaTransacao,
        Instant autorizadoEm,
        Instant recebidoEm
) {
}
