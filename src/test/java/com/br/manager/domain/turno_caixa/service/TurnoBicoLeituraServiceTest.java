package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.BicoLeituraFixture;
import com.br.manager.domain.turno_caixa.mapper.BicoLeituraMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoBicoLeituraServiceTest {

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private BicoLeituraRepository bicoLeituraRepository;

    @Spy
    private BicoLeituraMapper bicoLeituraMapper = new BicoLeituraMapper();

    @InjectMocks
    private TurnoBicoLeituraService service;

    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);
    private final UUID bicoId = UUID.randomUUID();

    private BicoLeituraInputDTO input(TipoLeituraBicoEnum type) {
        return BicoLeituraFixture.validoInput(bicoId, UUID.randomUUID(), type);
    }

    private void givenAbertoTurno() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
    }

    private void givenLeituraAlreadyRegistered(TipoLeituraBicoEnum type) {
        when(bicoLeituraRepository.existsByTurnoIdAndBicoIdAndTipoLeitura(turno.getId(), bicoId, type)).thenReturn(true);
    }

    private void returnSavedLeitura() {
        when(bicoLeituraRepository.saveAndFlush(any(LeituraBico.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRegisterFechamentoLeituraLinkedToTurno() {
        givenAbertoTurno();
        returnSavedLeitura();

        BicoLeituraResponseDTO response = service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.FECHAMENTO));

        ArgumentCaptor<LeituraBico> captor = ArgumentCaptor.forClass(LeituraBico.class);
        verify(bicoLeituraRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTurno()).isSameAs(turno);
        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response.getBicoId()).isEqualTo(bicoId);
        assertThat(response.getTipoLeitura()).isEqualTo(TipoLeituraBicoEnum.FECHAMENTO);
        assertThat(response.getLeituraMedidor()).isEqualTo(BicoLeituraFixture.MEDIDOR_LEITURA);
    }

    @Test
    void shouldValidateTurnoIsAbertoAndBicoBelongsToEstacao() {
        givenAbertoTurno();
        returnSavedLeitura();

        service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.ABERTURA));

        verify(validacaoSuporte).ensureTurnoIsAberto(turno);
        verify(validacaoSuporte).validateBicoBelongsToTurnoEstacao(turno.getEstacaoId(), bicoId);
    }

    @Test
    void shouldAllowRepeatedTestLeituras() {
        givenAbertoTurno();
        returnSavedLeitura();

        service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.TESTE));

        verify(bicoLeituraRepository, never()).existsByTurnoIdAndBicoIdAndTipoLeitura(any(), any(), any());
        verify(bicoLeituraRepository).saveAndFlush(any(LeituraBico.class));
    }

    @Test
    void shouldThrowExcecaoNegocioWhenPayloadIsNull() {
        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), null))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(validacaoSuporte, bicoLeituraRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenTurnoDoesNotExist() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.FECHAMENTO)))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verifyNoInteractions(bicoLeituraRepository);
    }

    @Test
    void shouldPropagateExcecaoNegocioWhenTurnoIsFechado() {
        givenAbertoTurno();
        doThrow(new ExcecaoNegocio("fechado")).when(validacaoSuporte).ensureTurnoIsAberto(turno);

        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.FECHAMENTO)))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(bicoLeituraRepository);
    }

    @Test
    void shouldPropagateExcecaoNegocioWhenBicoDoesNotBelongToEstacao() {
        givenAbertoTurno();
        doThrow(new ExcecaoNegocio("bico")).when(validacaoSuporte)
                .validateBicoBelongsToTurnoEstacao(turno.getEstacaoId(), bicoId);

        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.FECHAMENTO)))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(bicoLeituraRepository);
    }

    @Test
    void shouldThrowExcecaoNegocioWhenAberturaLeituraAlreadyExists() {
        givenAbertoTurno();
        givenLeituraAlreadyRegistered(TipoLeituraBicoEnum.ABERTURA);

        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.ABERTURA)))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("abertura")
                .hasMessageContaining(bicoId.toString());
        verify(bicoLeituraRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowExcecaoNegocioWhenFechamentoLeituraAlreadyExists() {
        givenAbertoTurno();
        givenLeituraAlreadyRegistered(TipoLeituraBicoEnum.FECHAMENTO);

        assertThatThrownBy(() -> service.registerBicoLeitura(turno.getId(), input(TipoLeituraBicoEnum.FECHAMENTO)))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("fechamento")
                .hasMessageContaining(bicoId.toString());
        verify(bicoLeituraRepository, never()).saveAndFlush(any());
    }
}
