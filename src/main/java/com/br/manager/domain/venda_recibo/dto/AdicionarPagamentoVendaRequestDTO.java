package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdicionarPagamentoVendaRequestDTO(
        @NotNull(message = "O ID de Pagamento method é obrigatório.") UUID pagamentoMethodId,
        @NotNull(message = "Valor é obrigatório.") BigDecimal valor,
        @NotNull(message = "Situacao é obrigatório.") SituacaoPagamento status,
        String referenciaTransacao,
        Instant autorizadoEm,
        Instant recebidoEm
) {
}
