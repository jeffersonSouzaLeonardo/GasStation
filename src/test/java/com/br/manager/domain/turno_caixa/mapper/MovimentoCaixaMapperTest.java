package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.MovimentoCaixaFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MovimentoCaixaMapperTest {

    private final MovimentoCaixaMapper mapper = new MovimentoCaixaMapper();
    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);

    @Test
    void shouldMapInputToEntityLinkingTurno() {
        MovimentoCaixaInputDTO input = MovimentoCaixaFixture.completeInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        MovimentoCaixa movimento = mapper.toEntity(input, turno);

        assertThat(movimento.getId()).isNull();
        assertThat(movimento.getTurno()).isSameAs(turno);
        assertThat(movimento)
                .usingRecursiveComparison()
                .ignoringFields("id", "turno")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullEntityWhenInputIsNull() {
        assertThat(mapper.toEntity(null, turno)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithTurnoId() {
        MovimentoCaixa movimento = MovimentoCaixaFixture.persistedEntity(turno);

        MovimentoCaixaResponseDTO response = mapper.toResponse(movimento);

        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("turnoId")
                .isEqualTo(movimento);
    }

    @Test
    void shouldMapNullTurnoIdWhenMovimentoHasNoTurno() {
        MovimentoCaixa movimento = MovimentoCaixaFixture.persistedEntity(null);

        assertThat(mapper.toResponse(movimento).getTurnoId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
