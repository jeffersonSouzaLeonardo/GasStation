package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPrecoMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPrecoRepository;
import com.br.manager.domain.aquisicao_preco.repository.TabelaPrecoRepository;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoViolacoes;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemPrecoServiceTest {

    @Mock
    private ItemPrecoRepository precoItemRepository;

    @Mock
    private TabelaPrecoRepository precoTabelaRepository;

    @Spy
    private ItemPrecoMapper precoItemMapper = new ItemPrecoMapper();

    @InjectMocks
    private ItemPrecoService precoItemService;

    private final TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(AquisicaoPrecoReferenciaFixture.ativoUsuario());

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<ItemPreco>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private void stubSaveAllReturningArgument() {
        when(precoItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class ReplaceByTabelaPreco {

        @Test
        void shouldDeleteExistingItensAndInsertNewOnes() {
            ItemPrecoInputDTO withId = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            withId.setId(UUID.randomUUID());
            ItemPrecoInputDTO withoutAtivo = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            withoutAtivo.setAtivo(null);
            ItemPrecoInputDTO inativo = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            inativo.setAtivo(false);
            ArgumentCaptor<List<ItemPreco>> captor = listCaptor();
            when(precoItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<ItemPrecoResponseDTO> response = precoItemService.replaceByTabelaPreco(tabelaPreco, List.of(withId, withoutAtivo, inativo));

            verify(precoItemRepository).deleteByTabelaPreco(tabelaPreco);
            List<ItemPreco> saved = captor.getValue();
            assertThat(saved).hasSize(3).allSatisfy(item -> {
                assertThat(item.getId()).isNull();
                assertThat(item.getTabelaPreco()).isSameAs(tabelaPreco);
            });
            assertThat(saved).extracting(ItemPreco::getAtivo).containsExactly(true, true, false);
            assertThat(response).hasSize(3);
            assertThat(response.get(0).getPrecoTabelaId()).isEqualTo(tabelaPreco.getId());
            assertThat(response.get(0).getPrecoUnitario()).isEqualByComparingTo(ItemPrecoFixture.PRECO_UNITARIO);
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsEmpty() {
            assertThat(precoItemService.replaceByTabelaPreco(tabelaPreco, List.of())).isEmpty();

            verify(precoItemRepository).deleteByTabelaPreco(tabelaPreco);
            verify(precoItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsNull() {
            assertThat(precoItemService.replaceByTabelaPreco(tabelaPreco, null)).isEmpty();

            verify(precoItemRepository).deleteByTabelaPreco(tabelaPreco);
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(precoItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());
            List<ItemPrecoInputDTO> input = List.of(ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID()));

            assertThatThrownBy(() -> precoItemService.replaceByTabelaPreco(tabelaPreco, input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            doThrow(new DataIntegrityViolationException("fk")).when(precoItemRepository).deleteByTabelaPreco(tabelaPreco);

            assertThatThrownBy(() -> precoItemService.replaceByTabelaPreco(tabelaPreco, List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(tabelaPreco.getNome())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveAllByTabelaPreco {

        @Test
        void shouldAttachItensToTabelaAndSave() {
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            ArgumentCaptor<List<ItemPreco>> captor = listCaptor();
            when(precoItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            List<ItemPrecoInputDTO> input = List.of(
                    ItemPrecoFixture.validoInput(null, UUID.randomUUID()),
                    ItemPrecoFixture.validoInput(null, UUID.randomUUID()));

            List<ItemPrecoResponseDTO> response = precoItemService.saveAllByTabelaPreco(tabelaPreco.getId(), input);

            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getTabelaPreco()).isSameAs(tabelaPreco));
            assertThat(response).extracting(ItemPrecoResponseDTO::getPrecoTabelaId).containsOnly(tabelaPreco.getId());
        }

        @Test
        void shouldSaveEmptyListWhenInputIsNull() {
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            stubSaveAllReturningArgument();

            assertThat(precoItemService.saveItemPrecos(tabelaPreco.getId(), null)).isEmpty();
            verify(precoItemRepository).saveAllAndFlush(List.of());
        }

        @Test
        void shouldThrowNotFoundWhenTabelaDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(precoTabelaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoItemService.saveAllByTabelaPreco(id, List.of()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
            verify(precoItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            when(precoItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> precoItemService.saveAllByTabelaPreco(tabelaPreco.getId(), List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            when(precoItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> precoItemService.saveAllByTabelaPreco(tabelaPreco.getId(), List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(tabelaPreco.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveItemPreco {

        @Test
        void shouldSaveSingleItemLinkedToTabela() {
            ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            when(precoItemRepository.saveAndFlush(any(ItemPreco.class))).thenAnswer(invocation -> {
                ItemPreco item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            ItemPrecoResponseDTO response = precoItemService.saveItemPreco(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getPrecoTabelaId()).isEqualTo(tabelaPreco.getId());
            assertThat(response.getProdutoId()).isEqualTo(input.getProdutoId());
            assertThat(response.getPrecoUnitario()).isEqualByComparingTo(ItemPrecoFixture.PRECO_UNITARIO);
        }

        @Test
        void shouldThrowNotFoundWhenTabelaDoesNotExist() {
            ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
            when(precoTabelaRepository.findById(input.getPrecoTabelaId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoItemService.saveItemPreco(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            when(precoItemRepository.saveAndFlush(any(ItemPreco.class))).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> precoItemService.saveItemPreco(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(tabelaPreco.getId(), UUID.randomUUID());
            when(precoTabelaRepository.findById(tabelaPreco.getId())).thenReturn(Optional.of(tabelaPreco));
            when(precoItemRepository.saveAndFlush(any(ItemPreco.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> precoItemService.saveItemPreco(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(tabelaPreco.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateAtivoItem() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(existing.getId());
            input.setPrecoUnitario(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(precoItemRepository.saveAndFlush(existing)).thenReturn(existing);

            ItemPrecoResponseDTO response = precoItemService.update(input);

            assertThat(response.getPrecoUnitario()).isEqualByComparingTo(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);
            assertThat(response.getPrecoTabelaId()).isEqualTo(tabelaPreco.getId());
            verify(precoTabelaRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherTabelaWhenInformed() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            TabelaPreco outrosTabela = TabelaPrecoFixture.persistedEntity(AquisicaoPrecoReferenciaFixture.ativoUsuario());
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(existing.getId());
            input.setPrecoTabelaId(outrosTabela.getId());
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(precoTabelaRepository.findById(outrosTabela.getId())).thenReturn(Optional.of(outrosTabela));
            when(precoItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(precoItemService.update(input).getPrecoTabelaId()).isEqualTo(outrosTabela.getId());
        }

        @Test
        void shouldThrowNotFoundWhenItemIsMissingOrInativo() {
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(UUID.randomUUID());
            when(precoItemRepository.findByIdAndAtivoTrue(input.getId())).thenReturn(null);

            assertThatThrownBy(() -> precoItemService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewTabelaDoesNotExist() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(existing.getId());
            input.setPrecoTabelaId(UUID.randomUUID());
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(precoTabelaRepository.findById(input.getPrecoTabelaId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoItemService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(existing.getId());
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(precoItemRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> precoItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            ItemPrecoInputDTO input = new ItemPrecoInputDTO();
            input.setId(existing.getId());
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(precoItemRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> precoItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar item de preço")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            when(precoItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            precoItemService.delete(existing.getId());

            verify(precoItemRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(precoItemRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> precoItemService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(precoItemRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            when(precoItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(AquisicaoPrecoViolacoes.constraintViolationException()).when(precoItemRepository).delete(existing);

            assertThatThrownBy(() -> precoItemService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            when(precoItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(precoItemRepository).delete(existing);

            assertThatThrownBy(() -> precoItemService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir item de preço")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoItensOfTabela() {
            when(precoItemRepository.findByTabelaPrecoIdAndAtivoTrue(tabelaPreco.getId()))
                    .thenReturn(List.of(ItemPrecoFixture.persistedEntity(tabelaPreco)));

            assertThat(precoItemService.findByTabelaPreco(tabelaPreco.getId())).hasSize(1);
            assertThat(precoItemService.findAllByTabelaPreco(tabelaPreco.getId())).hasSize(1);
        }

        @Test
        void shouldListAllAtivoItens() {
            when(precoItemRepository.findAllByAtivoTrue()).thenReturn(List.of(ItemPrecoFixture.persistedEntity(tabelaPreco)));

            assertThat(precoItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldFindAtivoItemById() {
            ItemPreco existing = ItemPrecoFixture.persistedEntity(tabelaPreco);
            when(precoItemRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(precoItemService.find(existing.getId()).getId()).isEqualTo(existing.getId());
            assertThat(precoItemService.getItemPrecoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenAtivoItemDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> precoItemService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> precoItemService.getItemPrecoEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }
}
