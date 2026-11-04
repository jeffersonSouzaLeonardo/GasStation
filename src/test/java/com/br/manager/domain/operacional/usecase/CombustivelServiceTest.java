package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.dto.CombustivelResponseDTO;
import com.br.manager.domain.operacional.entity.Combustivel;
import com.br.manager.domain.operacional.fixture.CombustivelFixture;
import com.br.manager.domain.operacional.mapper.CombustivelMapper;
import com.br.manager.domain.operacional.repository.CombustivelRepository;
import com.br.manager.domain.operacional.support.OperacionalBViolacoes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CombustivelServiceTest {

    private static final Long UNKNOWN_ID = 999L;

    @Mock
    private CombustivelRepository combustivelRepository;

    @Spy
    private CombustivelMapper combustivelMapper = new CombustivelMapper();

    @InjectMocks
    private CombustivelService combustivelService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnCombustivel() {
            when(combustivelRepository.saveAndFlush(any(Combustivel.class))).thenAnswer(invocation -> {
                Combustivel combustivel = invocation.getArgument(0);
                combustivel.setId(CombustivelFixture.ID);
                return combustivel;
            });

            CombustivelResponseDTO response = combustivelService.create(CombustivelFixture.validoInput());

            assertThat(response.getId()).isEqualTo(CombustivelFixture.ID);
            assertThat(response.getNome()).isEqualTo(CombustivelFixture.NAME);
            assertThat(response.getUnidade()).isEqualTo(CombustivelFixture.UNIDADE);
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            when(combustivelRepository.saveAndFlush(any(Combustivel.class))).thenThrow(OperacionalBViolacoes.violacoesOf(new Combustivel()));

            assertThatThrownBy(() -> combustivelService.create(CombustivelFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("O nome do combustível é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorIncludingCombustivelName() {
            when(combustivelRepository.saveAndFlush(any(Combustivel.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> combustivelService.create(CombustivelFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar combustível " + CombustivelFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapPersistenceErrorWithoutNameWhenNameIsVazio() {
            CombustivelInputDTO input = CombustivelFixture.validoInput();
            input.setNome(" ");
            when(combustivelRepository.saveAndFlush(any(Combustivel.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> combustivelService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar combustível ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingCombustivel() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            CombustivelInputDTO input = CombustivelFixture.outrosInput();
            input.setId(existing.getId());
            when(combustivelRepository.findByIdAndExcluidoIsNull(existing.getId())).thenReturn(existing);
            when(combustivelRepository.saveAndFlush(existing)).thenReturn(existing);

            CombustivelResponseDTO response = combustivelService.update(input);

            assertThat(response.getNome()).isEqualTo(CombustivelFixture.OUTROS_NAME);
        }

        @Test
        void shouldThrowNotFoundWhenCombustivelDoesNotExist() {
            CombustivelInputDTO input = CombustivelFixture.validoInput();
            input.setId(UNKNOWN_ID);

            assertThatThrownBy(() -> combustivelService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(UNKNOWN_ID.toString());
            verify(combustivelRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            CombustivelInputDTO input = CombustivelFixture.validoInput();
            input.setId(existing.getId());
            when(combustivelRepository.findByIdAndExcluidoIsNull(existing.getId())).thenReturn(existing);
            when(combustivelRepository.saveAndFlush(existing)).thenThrow(OperacionalBViolacoes.violacoesOf(new Combustivel()));

            assertThatThrownBy(() -> combustivelService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("A unidade de medida é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorIncludingCombustivelName() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            CombustivelInputDTO input = CombustivelFixture.validoInput();
            input.setId(existing.getId());
            when(combustivelRepository.findByIdAndExcluidoIsNull(existing.getId())).thenReturn(existing);
            when(combustivelRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> combustivelService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar combustível " + CombustivelFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListNotExcluidoCombustivels() {
            when(combustivelRepository.findAllByExcluidoIsNull()).thenReturn(List.of(CombustivelFixture.persistedEntity()));

            assertThat(combustivelService.findAll())
                    .extracting(CombustivelResponseDTO::getNome)
                    .containsExactly(CombustivelFixture.NAME);
        }

        @Test
        void shouldBuscaByName() {
            when(combustivelRepository.findByNameContainingIgnoreCaseAndExcluidoIsNull("gasolina"))
                    .thenReturn(List.of(CombustivelFixture.persistedEntity()));

            assertThat(combustivelService.findByDescricao("gasolina")).hasSize(1);
        }

        @Test
        void shouldFindById() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            when(combustivelRepository.findByIdAndExcluidoIsNull(existing.getId())).thenReturn(existing);

            assertThat(combustivelService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            assertThatThrownBy(() -> combustivelService.find(UNKNOWN_ID))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(UNKNOWN_ID.toString());
        }

        @Test
        void shouldReturnEntityById() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            when(combustivelRepository.findByIdAndExcluidoIsNull(existing.getId())).thenReturn(existing);

            assertThat(combustivelService.getCombustivelEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityIdDoesNotExist() {
            assertThatThrownBy(() -> combustivelService.getCombustivelEntityById(UNKNOWN_ID))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldSoftDeleteCombustivel() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            when(combustivelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            combustivelService.delete(existing.getId());

            ArgumentCaptor<Combustivel> captor = ArgumentCaptor.forClass(Combustivel.class);
            verify(combustivelRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getExcluido()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownCombustivel() {
            when(combustivelRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> combustivelService.delete(UNKNOWN_ID)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(combustivelRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            when(combustivelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(combustivelRepository.saveAndFlush(existing)).thenThrow(OperacionalBViolacoes.violacoesOf(new Combustivel()));

            assertThatThrownBy(() -> combustivelService.delete(existing.getId()))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("O status do combustível é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Combustivel existing = CombustivelFixture.persistedEntity();
            when(combustivelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(combustivelRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> combustivelService.delete(existing.getId()))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao deletar combustível ")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
