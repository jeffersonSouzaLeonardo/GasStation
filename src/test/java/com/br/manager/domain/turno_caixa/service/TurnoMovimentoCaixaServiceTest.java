package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.MovimentoCaixaFixture;
import com.br.manager.domain.turno_caixa.mapper.MovimentoCaixaMapper;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
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
class TurnoMovimentoCaixaServiceTest {

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private MovimentoCaixaRepository dinheiroMovimentoRepository;

    @Spy
    private MovimentoCaixaMapper dinheiroMovimentoMapper = new MovimentoCaixaMapper();

    @InjectMocks
    private TurnoMovimentoCaixaService service;

    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);
    private final MovimentoCaixaInputDTO input = MovimentoCaixaFixture.validoInput(UUID.randomUUID());

    @Test
    void shouldRegisterMovimentoCaixaLinkedToTurno() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(dinheiroMovimentoRepository.saveAndFlush(any(MovimentoCaixa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimentoCaixaResponseDTO response = service.registerMovimentoCaixa(turno.getId(), input);

        ArgumentCaptor<MovimentoCaixa> captor = ArgumentCaptor.forClass(MovimentoCaixa.class);
        verify(dinheiroMovimentoRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTurno()).isSameAs(turno);
        assertThat(captor.getValue().getValor()).isEqualTo(MovimentoCaixaFixture.VALOR);
        assertThat(response.getTurnoId()).isEqualTo(turno.getId());
        assertThat(response.getTipoMovimento()).isEqualTo(MovimentoCaixaFixture.MOVIMENTO_TYPE);
        assertThat(response.getCriadoPor()).isEqualTo(input.getCriadoPor());
    }

    @Test
    void shouldValidateAbertoTurnoAndCreator() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(dinheiroMovimentoRepository.saveAndFlush(any(MovimentoCaixa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.registerMovimentoCaixa(turno.getId(), input);

        verify(validacaoSuporte).ensureTurnoIsAberto(turno);
        verify(validacaoSuporte).getObrigatorioUsuario(input.getCriadoPor(), "criadoPor");
    }

    @Test
    void shouldThrowExcecaoNegocioWhenPayloadIsNull() {
        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), null))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(validacaoSuporte, dinheiroMovimentoRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenTurnoDoesNotExist() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verifyNoInteractions(dinheiroMovimentoRepository);
    }

    @Test
    void shouldPropagateExcecaoNegocioWhenTurnoIsFechado() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        doThrow(new ExcecaoNegocio("fechado")).when(validacaoSuporte).ensureTurnoIsAberto(turno);

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocio.class);
        verifyNoInteractions(dinheiroMovimentoRepository);
    }

    @Test
    void shouldThrowExcecaoNegocioWhenMovimentoTypeIsNull() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        input.setTipoMovimento(null);

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("tipo de movimentação");
        verifyNoInteractions(dinheiroMovimentoRepository);
    }

    @Test
    void shouldThrowExcecaoNegocioWhenValorIsNull() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        input.setValor(null);

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("valor");
        verifyNoInteractions(dinheiroMovimentoRepository);
    }

    @Test
    void shouldThrowExcecaoNegocioWhenValorIsNegative() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        input.setValor(MovimentoCaixaFixture.NEGATIVE_VALOR);

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("valor");
        verifyNoInteractions(dinheiroMovimentoRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenCreatorDoesNotExist() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(validacaoSuporte.getObrigatorioUsuario(input.getCriadoPor(), "criadoPor")).thenThrow(new ExcecaoNegocioNaoEncontrado("usuario"));

        assertThatThrownBy(() -> service.registerMovimentoCaixa(turno.getId(), input))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verify(dinheiroMovimentoRepository, never()).saveAndFlush(any());
    }
}
