package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoAlocacaoFixture;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.mapper.TurnoAlocacaoMapper;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoAlocacaoRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoAberturaServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private TurnoAlocacaoRepository alocacaoRepository;

    @Mock
    private BicoLeituraRepository bicoLeituraRepository;

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Spy
    private TurnoMapper turnoMapper = new TurnoMapper();

    @Spy
    private TurnoAlocacaoMapper turnoAlocacaoMapper = new TurnoAlocacaoMapper();

    @InjectMocks
    private TurnoAberturaService turnoAberturaService;

    private void givenTurnoIsSaved() {
        when(turnoRepository.saveAndFlush(any(Turno.class))).thenAnswer(invocation -> {
            Turno turno = invocation.getArgument(0);
            turno.setId(UUID.randomUUID());
            return turno;
        });
    }

    @Nested
    class AbertoTurno {

        @Test
        void shouldPersistAbertoTurnoAndReturnResponse() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            givenTurnoIsSaved();

            TurnoResponseDTO response = turnoAberturaService.abertoTurno(request);

            ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
            verify(turnoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
            assertThat(captor.getValue().getOpenedAt()).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
            assertThat(response.getEstacaoId()).isEqualTo(request.getEstacaoId());
            assertThat(response.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
        }

        @Test
        void shouldValidateEstacaoCaixeiroAndOpener() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            givenTurnoIsSaved();

            turnoAberturaService.abertoTurno(request);

            verify(validacaoSuporte).getObrigatorioEstacao(request.getEstacaoId());
            verify(validacaoSuporte).getObrigatorioUsuario(request.getCaixeiroUsuarioId(), "caixeiroUsuarioId");
            verify(validacaoSuporte).getObrigatorioUsuario(request.getOpenedBy(), "openedBy");
        }

        @Test
        void shouldRejectNullRequest() {
            assertThatThrownBy(() -> turnoAberturaService.abertoTurno(null))
                    .isInstanceOf(ExcecaoNegocio.class);
            verifyNoInteractions(turnoRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenEstacaoIsUnknown() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            doThrow(new ExcecaoNegocioNaoEncontrado("posto")).when(validacaoSuporte).getObrigatorioEstacao(request.getEstacaoId());

            assertThatThrownBy(() -> turnoAberturaService.abertoTurno(request))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(turnoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectWhenEstacaoAlreadyHasAbertoTurno() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            when(turnoRepository.existsByEstacaoIdAndStatus(request.getEstacaoId(), TurnoSituacaoEnum.ABERTO)).thenReturn(true);

            assertThatThrownBy(() -> turnoAberturaService.abertoTurno(request))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(request.getEstacaoId().toString());
            verify(turnoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectWhenCaixeiroAlreadyHasAbertoTurnoInEstacao() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            when(turnoRepository.existsByEstacaoIdAndCaixeiroUsuarioIdAndStatus(
                    request.getEstacaoId(), request.getCaixeiroUsuarioId(), TurnoSituacaoEnum.ABERTO)).thenReturn(true);

            assertThatThrownBy(() -> turnoAberturaService.abertoTurno(request))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(request.getCaixeiroUsuarioId().toString());
            verify(turnoRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Alocacoes {

        @Test
        void shouldPersistEachAlocacaoLinkedToSavedTurno() {
            TurnoAlocacaoInputDTO alocacaoInput = TurnoAlocacaoFixture.validoInput();
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            request.setAlocacoes(List.of(alocacaoInput));
            givenTurnoIsSaved();

            TurnoResponseDTO response = turnoAberturaService.abertoTurno(request);

            ArgumentCaptor<AlocacaoTurno> captor = ArgumentCaptor.forClass(AlocacaoTurno.class);
            verify(alocacaoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getTurno().getId()).isEqualTo(response.getId());
            assertThat(captor.getValue().getUsuarioId()).isEqualTo(alocacaoInput.getUsuarioId());
            assertThat(captor.getValue().getCargo()).isEqualTo(TurnoAlocacaoFixture.CARGO);
            verify(validacaoSuporte).getObrigatorioUsuario(alocacaoInput.getUsuarioId(), "alocacaoUsuarioId");
        }

        @Test
        void shouldSkipNullAlocacaoEntries() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            request.setAlocacoes(Arrays.asList(null, TurnoAlocacaoFixture.validoInput()));
            givenTurnoIsSaved();

            turnoAberturaService.abertoTurno(request);

            verify(alocacaoRepository, times(1)).saveAndFlush(any(AlocacaoTurno.class));
        }

        @Test
        void shouldNotPersistAlocacoesWhenListIsNull() {
            givenTurnoIsSaved();

            turnoAberturaService.abertoTurno(TurnoFixture.abertoRequest());

            verifyNoInteractions(alocacaoRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenAlocacaoUsuarioIsUnknown() {
            TurnoAlocacaoInputDTO alocacaoInput = TurnoAlocacaoFixture.validoInput();
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            request.setAlocacoes(List.of(alocacaoInput));
            givenTurnoIsSaved();
            lenient().doThrow(new ExcecaoNegocioNaoEncontrado("usuario"))
                    .when(validacaoSuporte).getObrigatorioUsuario(alocacaoInput.getUsuarioId(), "alocacaoUsuarioId");

            assertThatThrownBy(() -> turnoAberturaService.abertoTurno(request))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(alocacaoRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class AberturaBicoLeituras {

        @Test
        void shouldRegisterAberturaLeituraWithBicoMedidor() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            Bico bico = TurnoATestData.bico(UUID.randomUUID(), TurnoATestData.MEDIDOR_BICO);
            when(validacaoSuporte.findBicosByEstacaoId(request.getEstacaoId())).thenReturn(List.of(bico));
            givenTurnoIsSaved();

            TurnoResponseDTO response = turnoAberturaService.abertoTurno(request);

            ArgumentCaptor<LeituraBico> captor = ArgumentCaptor.forClass(LeituraBico.class);
            verify(bicoLeituraRepository).saveAndFlush(captor.capture());
            LeituraBico leitura = captor.getValue();
            assertThat(leitura.getTurno().getId()).isEqualTo(response.getId());
            assertThat(leitura.getBicoId()).isEqualTo(bico.getId());
            assertThat(leitura.getTipoLeitura()).isEqualTo(TipoLeituraBicoEnum.ABERTURA);
            assertThat(leitura.getLeituraMedidor()).isEqualByComparingTo(TurnoATestData.MEDIDOR_BICO);
            assertThat(leitura.getRegistradoPor()).isEqualTo(request.getOpenedBy());
            assertThat(leitura.getLidoEm()).isNotNull();
        }

        @Test
        void shouldUseZeroMedidorWhenBicoHasNoMedidorNumero() {
            TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
            Bico bico = TurnoATestData.bico(UUID.randomUUID(), null);
            when(validacaoSuporte.findBicosByEstacaoId(request.getEstacaoId())).thenReturn(List.of(bico));
            givenTurnoIsSaved();

            turnoAberturaService.abertoTurno(request);

            ArgumentCaptor<LeituraBico> captor = ArgumentCaptor.forClass(LeituraBico.class);
            verify(bicoLeituraRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getLeituraMedidor()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldNotRegisterLeiturasWhenEstacaoHasNoBicos() {
            givenTurnoIsSaved();

            turnoAberturaService.abertoTurno(TurnoFixture.abertoRequest());

            verifyNoInteractions(bicoLeituraRepository);
        }
    }
}
