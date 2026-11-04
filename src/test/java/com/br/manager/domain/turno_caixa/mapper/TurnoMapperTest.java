package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TurnoMapperTest {

    private final TurnoMapper mapper = new TurnoMapper();

    @Nested
    class AbertoRequestToEntity {

        @Test
        void shouldMapAbertoRequestFields() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();

            Turno turno = mapper.toEntity(request);

            assertThat(turno.getId()).isNull();
            assertThat(turno.getEstacaoId()).isEqualTo(request.getEstacaoId());
            assertThat(turno.getCaixeiroUsuarioId()).isEqualTo(request.getCaixeiroUsuarioId());
            assertThat(turno.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
            assertThat(turno.getOpenedBy()).isEqualTo(request.getOpenedBy());
            assertThat(turno.getObservacoes()).isEqualTo(TurnoFixture.OBSERVACOES);
        }

        @Test
        void shouldNotSetSituacaoNorOpenedAt() {
            Turno turno = mapper.toEntity(TurnoFixture.abertoRequest());

            assertThat(turno.getStatus()).isNull();
            assertThat(turno.getOpenedAt()).isNull();
        }

        @Test
        void shouldReturnNullWhenAbertoRequestIsNull() {
            assertThat(mapper.toEntity((TurnoAberturaRequestDTO) null)).isNull();
        }
    }

    @Nested
    class SincronizacaoToEntity {

        @Test
        void shouldMapSincronizacaoFieldsKeepingId() {
            TurnoSincronizacaoDTO dto = TurnoFixture.fechadoSincronizacao(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            Turno turno = mapper.toEntity(dto);

            assertThat(turno.getId()).isEqualTo(dto.getId());
            assertThat(turno.getEstacaoId()).isEqualTo(dto.getEstacaoId());
            assertThat(turno.getNome()).isEqualTo(TurnoFixture.NAME);
            assertThat(turno.getOpenedAt()).isEqualTo(TurnoFixture.OPENED_AT);
            assertThat(turno.getFechadoAt()).isEqualTo(TurnoFixture.FECHADO_AT);
            assertThat(turno.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
            assertThat(turno.getOpenedBy()).isEqualTo(dto.getOpenedBy());
            assertThat(turno.getFechadoBy()).isEqualTo(dto.getFechadoBy());
            assertThat(turno.getObservacoes()).isEqualTo(TurnoFixture.SINCRONIZACAO_OBSERVACOES);
        }

        @Test
        void shouldUseOpenedByAsCaixeiroAndZeroAberturaDinheiro() {
            TurnoSincronizacaoDTO dto = TurnoFixture.abertoSincronizacao();

            Turno turno = mapper.toEntity(dto);

            assertThat(turno.getCaixeiroUsuarioId()).isEqualTo(dto.getOpenedBy());
            assertThat(turno.getFundoAbertura()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldReturnNullWhenSincronizacaoIsNull() {
            assertThat(mapper.toEntity((TurnoSincronizacaoDTO) null)).isNull();
        }
    }

    @Nested
    class UpdateFromSincronizacao {

        @Test
        void shouldOverwriteSincronizacaoFieldsPreservingIdCaixeiroAndAberturaDinheiro() {
            Turno turno = TurnoFixture.abertoTurno();
            UUID originalId = turno.getId();
            UUID originalCaixeiro = turno.getCaixeiroUsuarioId();
            TurnoSincronizacaoDTO dto = TurnoFixture.fechadoSincronizacao(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            mapper.updateFromSincronizacao(dto, turno);

            assertThat(turno.getId()).isEqualTo(originalId);
            assertThat(turno.getCaixeiroUsuarioId()).isEqualTo(originalCaixeiro);
            assertThat(turno.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
            assertThat(turno.getEstacaoId()).isEqualTo(dto.getEstacaoId());
            assertThat(turno.getNome()).isEqualTo(TurnoFixture.NAME);
            assertThat(turno.getOpenedAt()).isEqualTo(TurnoFixture.OPENED_AT);
            assertThat(turno.getFechadoAt()).isEqualTo(TurnoFixture.FECHADO_AT);
            assertThat(turno.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
            assertThat(turno.getOpenedBy()).isEqualTo(dto.getOpenedBy());
            assertThat(turno.getFechadoBy()).isEqualTo(dto.getFechadoBy());
            assertThat(turno.getObservacoes()).isEqualTo(TurnoFixture.SINCRONIZACAO_OBSERVACOES);
        }

        @Test
        void shouldIgnoreNullDto() {
            Turno turno = TurnoFixture.abertoTurno();

            mapper.updateFromSincronizacao(null, turno);

            assertThat(turno.getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
            assertThat(turno.getObservacoes()).isEqualTo(TurnoFixture.OBSERVACOES);
        }

        @Test
        void shouldIgnoreNullTurno() {
            mapper.updateFromSincronizacao(TurnoFixture.abertoSincronizacao(), null);
        }
    }

    @Nested
    class ToResponse {

        @Test
        void shouldMapEntityToResponse() {
            Turno turno = TurnoFixture.fechadoTurno();

            TurnoResponseDTO response = mapper.toResponse(turno);

            assertThat(response.getId()).isEqualTo(turno.getId());
            assertThat(response.getEstacaoId()).isEqualTo(turno.getEstacaoId());
            assertThat(response.getCaixeiroUsuarioId()).isEqualTo(turno.getCaixeiroUsuarioId());
            assertThat(response.getOpenedAt()).isEqualTo(TurnoFixture.OPENED_AT);
            assertThat(response.getFechadoAt()).isEqualTo(TurnoFixture.FECHADO_AT);
            assertThat(response.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
            assertThat(response.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
            assertThat(response.getOpenedBy()).isEqualTo(turno.getOpenedBy());
            assertThat(response.getFechadoBy()).isEqualTo(turno.getFechadoBy());
            assertThat(response.getObservacoes()).isEqualTo(TurnoFixture.OBSERVACOES);
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(mapper.toResponse(null)).isNull();
        }
    }
}
