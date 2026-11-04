package com.br.manager.domain.venda_recibo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VendaItemResponseDTO(
        UUID id,
        Integer linhaNumero,
        UUID produtoId,
        UUID bicoId,
        UUID tanqueId,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorDesconto,
        BigDecimal medidorInicial,
        BigDecimal medidorFinal
) {
}
