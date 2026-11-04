package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
import com.br.manager.domain.aquisicao_preco.mapper.ItemRecebimentoMercadoriaMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemRecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.RecebimentoMercadoriaRepository;
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
class ItemRecebimentoMercadoriaServiceTest {

    @Mock
    private ItemRecebimentoMercadoriaRepository mercadoriaReciboItemRepository;

    @Mock
    private RecebimentoMercadoriaRepository mercadoriaReciboRepository;

    @Spy
    private ItemRecebimentoMercadoriaMapper mercadoriaReciboItemMapper = new ItemRecebimentoMercadoriaMapper();

    @InjectMocks
    private ItemRecebimentoMercadoriaService mercadoriaReciboItemService;

    private final RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<RecebimentoMercadoriaItem>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private void stubRecibo() {
        when(mercadoriaReciboRepository.findById(recebimentoMercadoria.getId())).thenReturn(Optional.of(recebimentoMercadoria));
    }

    private List<ItemRecebimentoMercadoriaInputDTO> twoItens() {
        return List.of(
                RecebimentoMercadoriaItemFixture.validoInput(null, UUID.randomUUID(), UUID.randomUUID()),
                RecebimentoMercadoriaItemFixture.validoInput(null, UUID.randomUUID(), null));
    }

    @Nested
    class SaveRecebimentoMercadoriaItem {

        @Test
        void shouldSaveItemLinkedToRecibo() {
            ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(recebimentoMercadoria.getId(), UUID.randomUUID(), UUID.randomUUID());
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAndFlush(any(RecebimentoMercadoriaItem.class))).thenAnswer(invocation -> {
                RecebimentoMercadoriaItem item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            ItemRecebimentoMercadoriaResponseDTO response = mercadoriaReciboItemService.saveRecebimentoMercadoriaItem(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getMercadoriaReciboId()).isEqualTo(recebimentoMercadoria.getId());
            assertThat(response.getProdutoId()).isEqualTo(input.getProdutoId());
            assertThat(response.getTanqueId()).isEqualTo(input.getTanqueId());
            assertThat(response.getQuantidade()).isEqualByComparingTo(RecebimentoMercadoriaItemFixture.QUANTITY);
            assertThat(response.getCustoUnitario()).isEqualByComparingTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
        }

        @Test
        void shouldThrowNotFoundWhenReciboDoesNotExist() {
            ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), null);

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItem(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getMercadoriaReciboId().toString());
            verify(mercadoriaReciboItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(recebimentoMercadoria.getId(), UUID.randomUUID(), null);
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAndFlush(any(RecebimentoMercadoriaItem.class)))
                    .thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItem(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(recebimentoMercadoria.getId(), UUID.randomUUID(), null);
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAndFlush(any(RecebimentoMercadoriaItem.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItem(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveRecebimentoMercadoriaItens {

        @Test
        void shouldAttachAllItensToRecibo() {
            stubRecibo();
            ArgumentCaptor<List<RecebimentoMercadoriaItem>> captor = listCaptor();
            when(mercadoriaReciboItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<ItemRecebimentoMercadoriaResponseDTO> response = mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(recebimentoMercadoria.getId(), twoItens());

            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getRecebimentoMercadoria()).isSameAs(recebimentoMercadoria));
            assertThat(response).extracting(ItemRecebimentoMercadoriaResponseDTO::getMercadoriaReciboId).containsOnly(recebimentoMercadoria.getId());
        }

        @Test
        void shouldReturnEmptyListWithoutSavingWhenInputIsEmpty() {
            stubRecibo();

            assertThat(mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(recebimentoMercadoria.getId(), List.of())).isEmpty();
            assertThat(mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(recebimentoMercadoria.getId(), null)).isEmpty();
            verify(mercadoriaReciboItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldThrowNotFoundWhenReciboDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(id, twoItens()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(recebimentoMercadoria.getId(), twoItens()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(recebimentoMercadoria.getId(), twoItens()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar itens do recebimento de mercadorias " + recebimentoMercadoria.getId())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ReplaceByRecebimentoMercadoria {

        @Test
        void shouldDeleteCurrentItensAndSaveNewOnes() {
            List<RecebimentoMercadoriaItem> current = List.of(RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria));
            stubRecibo();
            when(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(recebimentoMercadoria.getId())).thenReturn(current);
            ArgumentCaptor<List<RecebimentoMercadoriaItem>> captor = listCaptor();
            when(mercadoriaReciboItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<ItemRecebimentoMercadoriaResponseDTO> response = mercadoriaReciboItemService.replaceByRecebimentoMercadoria(recebimentoMercadoria.getId(), twoItens());

            verify(mercadoriaReciboItemRepository).deleteAll(current);
            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getRecebimentoMercadoria()).isSameAs(recebimentoMercadoria));
            assertThat(response).hasSize(2);
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsEmpty() {
            List<RecebimentoMercadoriaItem> current = List.of(RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria));
            stubRecibo();
            when(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(recebimentoMercadoria.getId())).thenReturn(current);

            assertThat(mercadoriaReciboItemService.replaceByRecebimentoMercadoria(recebimentoMercadoria.getId(), null)).isEmpty();
            verify(mercadoriaReciboItemRepository).deleteAll(current);
            verify(mercadoriaReciboItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldThrowNotFoundWhenReciboDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboItemService.replaceByRecebimentoMercadoria(id, twoItens()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboItemRepository, never()).deleteAll(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboItemService.replaceByRecebimentoMercadoria(recebimentoMercadoria.getId(), twoItens()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            stubRecibo();
            doThrow(new DataIntegrityViolationException("fk")).when(mercadoriaReciboItemRepository).deleteAll(anyList());

            assertThatThrownBy(() -> mercadoriaReciboItemService.replaceByRecebimentoMercadoria(recebimentoMercadoria.getId(), twoItens()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao substituir itens do recebimento de mercadorias " + recebimentoMercadoria.getId())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFields() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(existing.getId());
            input.setQuantidade(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(mercadoriaReciboItemRepository.saveAndFlush(existing)).thenReturn(existing);

            ItemRecebimentoMercadoriaResponseDTO response = mercadoriaReciboItemService.update(input);

            assertThat(response.getQuantidade()).isEqualByComparingTo(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);
            assertThat(response.getCustoUnitario()).isEqualByComparingTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
            verify(mercadoriaReciboRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherRecibo() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(RecebimentoMercadoriaFixture.persistedEntity());
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(existing.getId());
            input.setMercadoriaReciboId(recebimentoMercadoria.getId());
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            stubRecibo();
            when(mercadoriaReciboItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(mercadoriaReciboItemService.update(input).getMercadoriaReciboId()).isEqualTo(recebimentoMercadoria.getId());
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> mercadoriaReciboItemService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewReciboDoesNotExist() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(existing.getId());
            input.setMercadoriaReciboId(UUID.randomUUID());
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> mercadoriaReciboItemService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(existing.getId());
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(mercadoriaReciboItemRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
            input.setId(existing.getId());
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(mercadoriaReciboItemRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> mercadoriaReciboItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            mercadoriaReciboItemService.delete(existing.getId());

            verify(mercadoriaReciboItemRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboItemService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboItemRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(AquisicaoPrecoViolacoes.constraintViolationException()).when(mercadoriaReciboItemRepository).delete(existing);

            assertThatThrownBy(() -> mercadoriaReciboItemService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(mercadoriaReciboItemRepository).delete(existing);

            assertThatThrownBy(() -> mercadoriaReciboItemService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListByRecibo() {
            when(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(recebimentoMercadoria.getId()))
                    .thenReturn(List.of(RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria)));

            assertThat(mercadoriaReciboItemService.findByRecebimentoMercadoria(recebimentoMercadoria.getId())).hasSize(1);
        }

        @Test
        void shouldListAll() {
            when(mercadoriaReciboItemRepository.findAll()).thenReturn(List.of(RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria)));

            assertThat(mercadoriaReciboItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldFindById() {
            RecebimentoMercadoriaItem existing = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
            when(mercadoriaReciboItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(mercadoriaReciboItemService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboItemService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
