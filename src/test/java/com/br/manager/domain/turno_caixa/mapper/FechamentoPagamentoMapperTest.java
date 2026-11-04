package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.FechamentoPagamentoFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FechamentoPagamentoMapperTest {

    private final FechamentoPagamentoMapper mapper = new FechamentoPagamentoMapper();
    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);

    @Test
    void shouldMapInputToEntityLinkingTurno() {
        FechamentoPagamentoInputDTO input = FechamentoPagamentoFixture.validoInput(UUID.randomUUID());

        FechamentoPagamento fechamento = mapper.toEntity(input, turno);

        assertThat(fechamento.getId()).isNull();
        assertThat(fechamento.getTurno()).isSameAs(turno);
        assertThat(fechamento.getPagamentoMethodId()).isEqualTo(input.getPagamentoMethodId());
        assertThat(fechamento.getValorEsperado()).isEqualTo(FechamentoPagamentoFixture.ESPERADO_VALOR);
        assertThat(fechamento.getValorDeclarado()).isEqualTo(FechamentoPagamentoFixture.DECLARADO_VALOR);
        assertThat(fechamento.getReferenciaComprovante()).isEqualTo(FechamentoPagamentoFixture.COMPROVANTE_REFERENCIA);
    }

    @Test
    void shouldComputeDiferencaAsDeclaradoMinusEsperado() {
        FechamentoPagamento fechamento = mapper.toEntity(FechamentoPagamentoFixture.validoInput(UUID.randomUUID()), turno);

        assertThat(fechamento.getValorDiferenca()).isEqualByComparingTo(FechamentoPagamentoFixture.DIFERENCA_VALOR);
    }

    @Test
    void shouldReturnNullEntityWhenInputIsNull() {
        assertThat(mapper.toEntity(null, turno)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithTurnoId() {
        FechamentoPagamento fechamento = FechamentoPagamentoFixture.persistedEntity(turno, UUID.randomUUID());

        FechamentoPagamentoResponseDTO response = mapper.toResponse(fechamento);

        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("turnoId")
                .isEqualTo(fechamento);
    }

    @Test
    void shouldMapNullTurnoIdWhenFechamentoHasNoTurno() {
        FechamentoPagamento fechamento = FechamentoPagamentoFixture.persistedEntity(null, UUID.randomUUID());

        assertThat(mapper.toResponse(fechamento).getTurnoId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
