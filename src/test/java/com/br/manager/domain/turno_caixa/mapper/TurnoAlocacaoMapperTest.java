package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.fixture.TurnoAlocacaoFixture;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TurnoAlocacaoMapperTest {

    private final TurnoAlocacaoMapper mapper = new TurnoAlocacaoMapper();

    @Test
    void shouldMapInputToEntityLinkingTurno() {
        Turno turno = TurnoFixture.abertoTurno();
        TurnoAlocacaoInputDTO input = TurnoAlocacaoFixture.validoInput();

        AlocacaoTurno alocacao = mapper.toEntity(input, turno);

        assertThat(alocacao.getId()).isNull();
        assertThat(alocacao.getTurno()).isSameAs(turno);
        assertThat(alocacao.getUsuarioId()).isEqualTo(input.getUsuarioId());
        assertThat(alocacao.getCargo()).isEqualTo(TurnoAlocacaoFixture.CARGO);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null, TurnoFixture.abertoTurno())).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Turno turno = TurnoFixture.abertoTurno();
        AlocacaoTurno alocacao = TurnoAlocacaoFixture.persistedEntity(turno, UUID.randomUUID());

        TurnoAlocacaoResponseDTO response = mapper.toResponse(alocacao);

        assertThat(response.getId()).isEqualTo(alocacao.getId());
        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response.getUsuarioId()).isEqualTo(alocacao.getUsuarioId());
        assertThat(response.getCargo()).isEqualTo(TurnoAlocacaoFixture.CARGO);
    }

    @Test
    void shouldMapNullTurnoIdWhenAlocacaoHasNoTurno() {
        AlocacaoTurno alocacao = TurnoAlocacaoFixture.persistedEntity(null, UUID.randomUUID());

        TurnoAlocacaoResponseDTO response = mapper.toResponse(alocacao);

        assertThat(response.getTurnoId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
