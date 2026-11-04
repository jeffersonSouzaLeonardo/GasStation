package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.fixture.TurnoAlocacaoFixture;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.mapper.TurnoAlocacaoMapper;
import com.br.manager.domain.turno_caixa.repository.TurnoAlocacaoRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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
class TurnoAlocacaoServiceTest {

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private TurnoAlocacaoRepository alocacaoRepository;

    @Spy
    private TurnoAlocacaoMapper alocacaoMapper = new TurnoAlocacaoMapper();

    @InjectMocks
    private TurnoAlocacaoService turnoAlocacaoService;

    @Nested
    class AddOrUpdateAlocacao {

        @Test
        void shouldCreateAlocacaoWhenUsuarioIsNotAllocatedYet() {
            Turno turno = TurnoFixture.abertoTurno();
            TurnoAlocacaoInputDTO input = TurnoAlocacaoFixture.validoInput();
            when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
            when(alocacaoRepository.findByTurnoIdAndUsuarioId(turno.getId(), input.getUsuarioId())).thenReturn(Optional.empty());
            when(alocacaoRepository.saveAndFlush(any(AlocacaoTurno.class))).thenAnswer(invocation -> {
                AlocacaoTurno alocacao = invocation.getArgument(0);
                alocacao.setId(UUID.randomUUID());
                return alocacao;
            });

            TurnoAlocacaoResponseDTO response = turnoAlocacaoService.addOrUpdateAlocacao(turno.getId(), input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getTurnoId()).isEqualTo(turno.getId());
            assertThat(response.getUsuarioId()).isEqualTo(input.getUsuarioId());
            assertThat(response.getCargo()).isEqualTo(TurnoAlocacaoFixture.CARGO);
        }

        @Test
        void shouldUpdateCargoWhenUsuarioIsAlreadyAllocated() {
            Turno turno = TurnoFixture.abertoTurno();
            AlocacaoTurno existing = TurnoAlocacaoFixture.persistedEntity(turno, UUID.randomUUID());
            TurnoAlocacaoInputDTO input = TurnoAlocacaoFixture.input(existing.getUsuarioId(), TurnoAlocacaoFixture.OUTROS_CARGO);
            when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
            when(alocacaoRepository.findByTurnoIdAndUsuarioId(turno.getId(), existing.getUsuarioId())).thenReturn(Optional.of(existing));
            when(alocacaoRepository.saveAndFlush(any(AlocacaoTurno.class))).thenAnswer(invocation -> invocation.getArgument(0));

            TurnoAlocacaoResponseDTO response = turnoAlocacaoService.addOrUpdateAlocacao(turno.getId(), input);

            ArgumentCaptor<AlocacaoTurno> captor = ArgumentCaptor.forClass(AlocacaoTurno.class);
            verify(alocacaoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue()).isSameAs(existing);
            assertThat(captor.getValue().getCargo()).isEqualTo(TurnoAlocacaoFixture.OUTROS_CARGO);
            assertThat(response.getId()).isEqualTo(existing.getId());
            verify(alocacaoMapper, never()).toEntity(any(), any());
        }

        @Test
        void shouldRejectNullRequest() {
            assertThatThrownBy(() -> turnoAlocacaoService.addOrUpdateAlocacao(UUID.randomUUID(), null))
                    .isInstanceOf(ExcecaoNegocio.class);
            verifyNoInteractions(validacaoSuporte, alocacaoRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenTurnoIsUnknown() {
            UUID turnoId = UUID.randomUUID();
            when(validacaoSuporte.getObrigatorioTurno(turnoId)).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

            assertThatThrownBy(() -> turnoAlocacaoService.addOrUpdateAlocacao(turnoId, TurnoAlocacaoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verifyNoInteractions(alocacaoRepository);
        }

        @Test
        void shouldPropagateBusinessErrorWhenTurnoIsFechado() {
            Turno turno = TurnoFixture.fechadoTurno();
            when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
            doThrow(new ExcecaoNegocio("fechado")).when(validacaoSuporte).ensureTurnoIsAberto(turno);

            assertThatThrownBy(() -> turnoAlocacaoService.addOrUpdateAlocacao(turno.getId(), TurnoAlocacaoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class);
            verifyNoInteractions(alocacaoRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenUsuarioIsUnknown() {
            Turno turno = TurnoFixture.abertoTurno();
            TurnoAlocacaoInputDTO input = TurnoAlocacaoFixture.validoInput();
            when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
            when(validacaoSuporte.getObrigatorioUsuario(input.getUsuarioId(), "alocacaoUsuarioId"))
                    .thenThrow(new ExcecaoNegocioNaoEncontrado("usuario"));

            assertThatThrownBy(() -> turnoAlocacaoService.addOrUpdateAlocacao(turno.getId(), input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verifyNoInteractions(alocacaoRepository);
        }
    }
}
