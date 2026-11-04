package com.br.manager.domain.venda_recibo.dto;

import java.time.Instant;
import java.util.UUID;

public record VendaReembolsoResponseDTO(
        UUID id,
        String motivo,
        UUID requestedBy,
        UUID aprovadoPor,
        Instant reembolsadoEm,
        String observacoes
) {
}
