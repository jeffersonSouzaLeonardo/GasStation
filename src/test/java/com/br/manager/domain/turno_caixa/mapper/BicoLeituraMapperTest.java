package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.BicoLeituraFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BicoLeituraMapperTest {

    private final BicoLeituraMapper mapper = new BicoLeituraMapper();
    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);

    @Test
    void shouldMapInputToEntityLinkingTurno() {
        BicoLeituraInputDTO input = BicoLeituraFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), TipoLeituraBicoEnum.FECHAMENTO);

        LeituraBico leitura = mapper.toEntity(input, turno);

        assertThat(leitura.getId()).isNull();
        assertThat(leitura.getTurno()).isSameAs(turno);
        assertThat(leitura)
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
        LeituraBico leitura = BicoLeituraFixture.persistedEntity(turno, UUID.randomUUID(), UUID.randomUUID(), TipoLeituraBicoEnum.ABERTURA);

        BicoLeituraResponseDTO response = mapper.toResponse(leitura);

        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("turnoId")
                .isEqualTo(leitura);
    }

    @Test
    void shouldMapNullTurnoIdWhenLeituraHasNoTurno() {
        LeituraBico leitura = BicoLeituraFixture.persistedEntity(null, UUID.randomUUID(), UUID.randomUUID(), TipoLeituraBicoEnum.TESTE);

        assertThat(mapper.toResponse(leitura).getTurnoId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
