package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.FechamentoPagamentoFixture;
import com.br.manager.domain.turno_caixa.mapper.FechamentoPagamentoMapper;
import com.br.manager.domain.turno_caixa.repository.FechamentoPagamentoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoFechamentoPagamentoServiceTest {

    private static final BigDecimal ATUALIZADO_DECLARADO_VALOR = new BigDecimal("510.00");
    private static final BigDecimal ATUALIZADO_DIFERENCA = new BigDecimal("10.00");

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private FechamentoPagamentoRepository pagamentoFechamentoRepository;

    @Spy
    private FechamentoPagamentoMapper pagamentoFechamentoMapper = new FechamentoPagamentoMapper();

    @InjectMocks
    private TurnoFechamentoPagamentoService service;

    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);
    private final UUID pagamentoMethodId = UUID.randomUUID();
    private final FechamentoPagamentoInputDTO input = FechamentoPagamentoFixture.validoInput(pagamentoMethodId);

    private void givenAbertoTurno() {
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
    }

    private FechamentoPagamento captureSavedFechamento() {
        ArgumentCaptor<FechamentoPagamento> captor = ArgumentCaptor.forClass(FechamentoPagamento.class);
        verify(pagamentoFechamentoRepository).saveAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class Register {

        @Test
        void shouldCreateFechamentoWithComputedDiferenca() {
            givenAbertoTurno();
            when(pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turno.getId())).thenReturn(List.of());
            when(pagamentoFechamentoRepository.saveAndFlush(any(FechamentoPagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            FechamentoPagamentoResponseDTO response = service.registerFechamentoPagamento(turno.getId(), input);

            FechamentoPagamento saved = captureSavedFechamento();
            assertThat(saved.getTurno()).isSameAs(turno);
            assertThat(saved.getPagamentoMethodId()).isEqualTo(pagamentoMethodId);
            assertThat(saved.getValorDiferenca()).isEqualByComparingTo(FechamentoPagamentoFixture.DIFERENCA_VALOR);
            assertThat(response.getTurnoId()).isEqualTo(turno.getId());
            assertThat(response.getValorEsperado()).isEqualTo(FechamentoPagamentoFixture.ESPERADO_VALOR);
            assertThat(response.getValorDeclarado()).isEqualTo(FechamentoPagamentoFixture.DECLARADO_VALOR);
            assertThat(response.getReferenciaComprovante()).isEqualTo(FechamentoPagamentoFixture.COMPROVANTE_REFERENCIA);
        }

        @Test
        void shouldUpdateExistingFechamentoOfSameMetodoPagamento() {
            givenAbertoTurno();
            FechamentoPagamento existing = FechamentoPagamentoFixture.persistedEntity(turno, pagamentoMethodId);
            FechamentoPagamento outrosMethod = FechamentoPagamentoFixture.persistedEntity(turno, UUID.randomUUID());
            when(pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turno.getId())).thenReturn(List.of(outrosMethod, existing));
            when(pagamentoFechamentoRepository.saveAndFlush(existing)).thenReturn(existing);
            input.setValorDeclarado(ATUALIZADO_DECLARADO_VALOR);

            FechamentoPagamentoResponseDTO response = service.registerFechamentoPagamento(turno.getId(), input);

            FechamentoPagamento saved = captureSavedFechamento();
            assertThat(saved).isSameAs(existing);
            assertThat(saved.getValorDeclarado()).isEqualTo(ATUALIZADO_DECLARADO_VALOR);
            assertThat(saved.getValorDiferenca()).isEqualByComparingTo(ATUALIZADO_DIFERENCA);
            assertThat(response.getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldValidateTurnoIsAberto() {
            givenAbertoTurno();
            when(pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turno.getId())).thenReturn(List.of());
            when(pagamentoFechamentoRepository.saveAndFlush(any(FechamentoPagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

            service.registerFechamentoPagamento(turno.getId(), input);

            verify(validacaoSuporte).ensureTurnoIsAberto(turno);
        }
    }

    @Nested
    class Validacao {

        @Test
        void shouldThrowExcecaoNegocioWhenPayloadIsNull() {
            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), null))
                    .isInstanceOf(ExcecaoNegocio.class);
            verifyNoInteractions(validacaoSuporte, pagamentoFechamentoRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenTurnoDoesNotExist() {
            when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verifyNoInteractions(pagamentoFechamentoRepository);
        }

        @Test
        void shouldPropagateExcecaoNegocioWhenTurnoIsFechado() {
            givenAbertoTurno();
            doThrow(new ExcecaoNegocio("fechado")).when(validacaoSuporte).ensureTurnoIsAberto(turno);

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocio.class);
            verifyNoInteractions(pagamentoFechamentoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenEsperadoValorIsNull() {
            givenAbertoTurno();
            input.setValorEsperado(null);

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("valor esperado");
            verifyNoInteractions(pagamentoFechamentoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenEsperadoValorIsNegative() {
            givenAbertoTurno();
            input.setValorEsperado(FechamentoPagamentoFixture.NEGATIVE_VALOR);

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("valor esperado");
            verifyNoInteractions(pagamentoFechamentoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenDeclaradoValorIsNull() {
            givenAbertoTurno();
            input.setValorDeclarado(null);

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("valor declarado");
            verifyNoInteractions(pagamentoFechamentoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenDeclaradoValorIsNegative() {
            givenAbertoTurno();
            input.setValorDeclarado(FechamentoPagamentoFixture.NEGATIVE_VALOR);

            assertThatThrownBy(() -> service.registerFechamentoPagamento(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("valor declarado");
            verifyNoInteractions(pagamentoFechamentoRepository);
        }
    }
}
