package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoFechamentoServiceTest {

    private static final UUID FECHADO_BY = UUID.randomUUID();

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private BicoLeituraRepository bicoLeituraRepository;

    @Spy
    private TurnoMapper turnoMapper = new TurnoMapper();

    @InjectMocks
    private TurnoFechamentoService turnoFechamentoService;

    @Test
    void shouldCloseAbertoTurno() {
        Turno turno = TurnoFixture.abertoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(turnoRepository.saveAndFlush(any(Turno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TurnoResponseDTO response = turnoFechamentoService.closeTurno(turno.getId(), TurnoFixture.closeRequest(FECHADO_BY));

        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
        assertThat(captor.getValue().getFechadoAt()).isNotNull();
        assertThat(captor.getValue().getFechadoBy()).isEqualTo(FECHADO_BY);
        assertThat(response.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
        assertThat(response.getFechadoBy()).isEqualTo(FECHADO_BY);
    }

    @Test
    void shouldCloseWhenEveryBicoHasFechamentoLeitura() {
        Turno turno = TurnoFixture.abertoTurno();
        Bico bico = TurnoATestData.bico(UUID.randomUUID(), TurnoATestData.MEDIDOR_BICO);
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(validacaoSuporte.findBicosByEstacaoId(turno.getEstacaoId())).thenReturn(List.of(bico));
        when(bicoLeituraRepository.existsByTurnoIdAndBicoIdAndTipoLeitura(
                turno.getId(), bico.getId(), TipoLeituraBicoEnum.FECHAMENTO)).thenReturn(true);
        when(turnoRepository.saveAndFlush(any(Turno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TurnoResponseDTO response = turnoFechamentoService.closeTurno(turno.getId(), TurnoFixture.closeRequest(FECHADO_BY));

        assertThat(response.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
    }

    @Test
    void shouldRejectWhenBicoHasNoFechamentoLeitura() {
        Turno turno = TurnoFixture.abertoTurno();
        Bico bico = TurnoATestData.bico(UUID.randomUUID(), TurnoATestData.MEDIDOR_BICO);
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(validacaoSuporte.findBicosByEstacaoId(turno.getEstacaoId())).thenReturn(List.of(bico));

        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(turno.getId(), TurnoFixture.closeRequest(FECHADO_BY)))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining(bico.getId().toString());
        verify(turnoRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldRejectNullRequest() {
        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(UUID.randomUUID(), null))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(validacaoSuporte, turnoRepository);
    }

    @Test
    void shouldRejectRequestWithoutFechadoBy() {
        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(UUID.randomUUID(), TurnoFixture.closeRequest(null)))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(validacaoSuporte, turnoRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenTurnoIsUnknown() {
        UUID turnoId = UUID.randomUUID();
        when(validacaoSuporte.getObrigatorioTurno(turnoId)).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(turnoId, TurnoFixture.closeRequest(FECHADO_BY)))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verifyNoInteractions(turnoRepository);
    }

    @Test
    void shouldRejectAlreadyFechadoTurno() {
        Turno turno = TurnoFixture.fechadoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);

        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(turno.getId(), TurnoFixture.closeRequest(FECHADO_BY)))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining(turno.getId().toString());
        verify(turnoRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldPropagateNotFoundWhenFechamentoUsuarioIsUnknown() {
        Turno turno = TurnoFixture.abertoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(validacaoSuporte.getObrigatorioUsuario(FECHADO_BY, "fechadoBy")).thenThrow(new ExcecaoNegocioNaoEncontrado("usuario"));

        assertThatThrownBy(() -> turnoFechamentoService.closeTurno(turno.getId(), TurnoFixture.closeRequest(FECHADO_BY)))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verify(turnoRepository, never()).saveAndFlush(any());
    }
}
