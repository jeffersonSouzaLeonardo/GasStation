package com.br.manager.domain.venda_recibo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReembolsarVendaRequestDTO(
        @NotBlank(message = "Reason é obrigatório.") String motivo,
        @NotNull(message = "Requested by é obrigatório.") UUID requestedBy,
        @NotNull(message = "Aprovado by é obrigatório.") UUID aprovadoPor,
        Instant reembolsadoEm,
        String observacoes
) {
}
