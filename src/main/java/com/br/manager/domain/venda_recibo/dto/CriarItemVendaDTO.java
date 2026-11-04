package com.br.manager.domain.venda_recibo.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarItemVendaDTO(
        @NotNull(message = "Linha numero é obrigatório.") Integer linhaNumero,
        @NotNull(message = "O ID de Produto é obrigatório.") UUID produtoId,
        UUID bicoId,
        UUID tanqueId,
        @NotNull(message = "Quantity é obrigatório.") BigDecimal quantidade,
        @NotNull(message = "Unidade preco é obrigatório.") BigDecimal precoUnitario,
        BigDecimal valorDesconto,
        BigDecimal medidorInicial,
        BigDecimal medidorFinal
) {
}
