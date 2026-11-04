package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Movimentacao de Caixa do turno.
 */
public final class MovimentoCaixaFixture {

    public static final TipoMovimentoCaixaEnum MOVIMENTO_TYPE = TipoMovimentoCaixaEnum.SANGRIA;
    public static final BigDecimal VALOR = new BigDecimal("150.00");
    public static final BigDecimal NEGATIVE_VALOR = new BigDecimal("-0.01");
    public static final LocalDateTime OCORRIDO_EM = LocalDateTime.of(2026, 10, 31, 10, 30);
    public static final String REFERENCIA = "SANGRIA-001";
    public static final String REASON = "Sangria para o cofre";

    private MovimentoCaixaFixture() {
    }

    public static MovimentoCaixaInputDTO validoInput(UUID criadoPor) {
        MovimentoCaixaInputDTO input = new MovimentoCaixaInputDTO();
        input.setTipoMovimento(MOVIMENTO_TYPE);
        input.setValor(VALOR);
        input.setOcorridoEm(OCORRIDO_EM);
        input.setReferencia(REFERENCIA);
        input.setMotivo(REASON);
        input.setCriadoPor(criadoPor);
        return input;
    }

    public static MovimentoCaixaInputDTO completeInput(UUID criadoPor, UUID aprovadoPor, UUID pagamentoMethodId) {
        MovimentoCaixaInputDTO input = validoInput(criadoPor);
        input.setAprovadoPor(aprovadoPor);
        input.setPagamentoMethodId(pagamentoMethodId);
        return input;
    }

    public static MovimentoCaixa persistedEntity(Turno turno) {
        MovimentoCaixa movimento = new MovimentoCaixa();
        movimento.setId(UUID.randomUUID());
        movimento.setTurno(turno);
        movimento.setTipoMovimento(MOVIMENTO_TYPE);
        movimento.setPagamentoMethodId(UUID.randomUUID());
        movimento.setValor(VALOR);
        movimento.setOcorridoEm(OCORRIDO_EM);
        movimento.setReferencia(REFERENCIA);
        movimento.setMotivo(REASON);
        movimento.setCriadoPor(UUID.randomUUID());
        movimento.setAprovadoPor(UUID.randomUUID());
        return movimento;
    }
}
