package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CriarVendaRequestDTO(
        @NotNull(message = "O ID de Estacao é obrigatório.") UUID estacaoId,
        @NotNull(message = "O ID de Turno é obrigatório.") UUID turnoId,
        @NotBlank(message = "Venda numero é obrigatório.") String numeroVenda,
        @NotNull(message = "Vendido at é obrigatório.") Instant vendidoEm,
        UUID clienteId,
        UUID veiculoId,
        UUID frentistaUsuarioId,
        @NotNull(message = "O ID de Caixeiro usuario é obrigatório.") UUID caixeiroUsuarioId,
        BigDecimal valorDesconto,
        SituacaoFiscal situacaoFiscal,
        String observacoes,
        @Valid @NotEmpty(message = "Venda deve contain at least one item.") List<CriarItemVendaDTO> itens
) {
}
