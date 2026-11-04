package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.dto.EstacaoResponseDTO;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organizacao.fixture.EstacaoFixture;
import com.br.manager.domain.organizacao.mapper.EstacaoMapper;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstacaoServiceTest {

    private static final String NAME_OBRIGATORIO = "O nome do posto é obrigatório.";

    @Mock
    private EstacaoRepository estacaoRepository;

    @Spy
    private EstacaoMapper estacaoMapper = new EstacaoMapper();

    @InjectMocks
    private EstacaoService estacaoService;

    private static ConstraintViolationException nomeViolation() {
        Estacao invalido = EstacaoFixture.persistedEntity();
        invalido.setNome(null);
        return ConstraintViolationFixture.violacoesOf(invalido);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnEstacao() {
            when(estacaoRepository.saveAndFlush(any(Estacao.class))).thenAnswer(invocation -> {
                Estacao estacao = invocation.getArgument(0);
                estacao.setId(UUID.randomUUID());
                return estacao;
            });

            EstacaoResponseDTO response = estacaoService.create(EstacaoFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpj()).isEqualTo(EstacaoFixture.CNPJ);
            assertThat(response.getFusoHorario()).isEqualTo(EstacaoFixture.FusoHorario);
        }

        @Test
        void shouldApplyDefaultTimezoneWhenNotInformed() {
            EstacaoInputDTO input = EstacaoFixture.validoInput();
            input.setFusoHorario(" ");
            when(estacaoRepository.saveAndFlush(any(Estacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

            estacaoService.create(input);

            ArgumentCaptor<Estacao> captor = ArgumentCaptor.forClass(Estacao.class);
            verify(estacaoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getFusoHorario()).isEqualTo(EstacaoFixture.FusoHorario);
        }

        @Test
        void shouldKeepInformedTimezone() {
            EstacaoInputDTO input = EstacaoFixture.validoInput();
            input.setFusoHorario(EstacaoFixture.OUTROS_TIMEZONE);
            when(estacaoRepository.saveAndFlush(any(Estacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(estacaoService.create(input).getFusoHorario()).isEqualTo(EstacaoFixture.OUTROS_TIMEZONE);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(estacaoRepository.saveAndFlush(any(Estacao.class))).thenThrow(nomeViolation());

            assertThatThrownBy(() -> estacaoService.create(EstacaoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(NAME_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(estacaoRepository.saveAndFlush(any(Estacao.class)))
                    .thenThrow(new DataIntegrityViolationException("fk empresa"));

            assertThatThrownBy(() -> estacaoService.create(EstacaoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(EstacaoFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWhenInputIsNull() {
            assertThatThrownBy(() -> estacaoService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar posto ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingEstacao() {
            Estacao existing = EstacaoFixture.persistedEntity();
            EstacaoInputDTO input = EstacaoFixture.validoInput(existing.getEmpresaId());
            input.setId(existing.getId());
            input.setNome("Posto Atualizado");
            when(estacaoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoRepository.saveAndFlush(existing)).thenReturn(existing);

            EstacaoResponseDTO response = estacaoService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getNome()).isEqualTo("Posto Atualizado");
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoDoesNotExist() {
            EstacaoInputDTO input = EstacaoFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> estacaoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(estacaoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Estacao existing = EstacaoFixture.persistedEntity();
            EstacaoInputDTO input = EstacaoFixture.validoInput();
            input.setId(existing.getId());
            when(estacaoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoRepository.saveAndFlush(existing)).thenThrow(nomeViolation());

            assertThatThrownBy(() -> estacaoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(NAME_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Estacao existing = EstacaoFixture.persistedEntity();
            EstacaoInputDTO input = EstacaoFixture.validoInput();
            input.setId(existing.getId());
            when(estacaoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> estacaoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(EstacaoFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoEstacaos() {
            when(estacaoRepository.findAllByAtivoTrue()).thenReturn(List.of(EstacaoFixture.persistedEntity()));

            assertThat(estacaoService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByName() {
            when(estacaoRepository.findByNameContainingIgnoreCaseAndAtivoTrue("centro"))
                    .thenReturn(List.of(EstacaoFixture.persistedEntity()));

            assertThat(estacaoService.findByName("centro")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(estacaoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(estacaoService.findByDescricao(" ")).isEmpty();
            verify(estacaoRepository, never()).findByNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Estacao existing = EstacaoFixture.persistedEntity();
            when(estacaoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(estacaoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            Estacao existing = EstacaoFixture.persistedEntity();
            when(estacaoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(estacaoService.getEstacaoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> estacaoService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> estacaoService.getEstacaoEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateEstacao() {
            Estacao existing = EstacaoFixture.persistedEntity();
            when(estacaoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            estacaoService.delete(existing.getId());

            ArgumentCaptor<Estacao> captor = ArgumentCaptor.forClass(Estacao.class);
            verify(estacaoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownEstacao() {
            UUID id = UUID.randomUUID();
            when(estacaoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> estacaoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Estacao existing = EstacaoFixture.persistedEntity();
            when(estacaoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(estacaoRepository.saveAndFlush(existing)).thenThrow(nomeViolation());

            assertThatThrownBy(() -> estacaoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(NAME_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Estacao existing = EstacaoFixture.persistedEntity();
            when(estacaoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(estacaoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> estacaoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir posto")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
