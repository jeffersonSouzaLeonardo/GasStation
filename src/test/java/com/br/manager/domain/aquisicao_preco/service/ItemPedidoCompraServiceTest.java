package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPedidoCompraMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoViolacoes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemPedidoCompraServiceTest {

    @Mock
    private ItemPedidoCompraRepository compraOrderItemRepository;

    @Mock
    private PedidoCompraRepository compraOrderRepository;

    @Spy
    private ItemPedidoCompraMapper compraOrderItemMapper = new ItemPedidoCompraMapper();

    @InjectMocks
    private ItemPedidoCompraService compraOrderItemService;

    private final PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<PedidoCompraItem>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    @Nested
    class Create {

        @Test
        void shouldCreateItemLinkedToOrder() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            ArgumentCaptor<PedidoCompraItem> captor = ArgumentCaptor.forClass(PedidoCompraItem.class);
            when(compraOrderItemRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                PedidoCompraItem item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            ItemPedidoCompraResponseDTO response = compraOrderItemService.create(input);

            assertThat(captor.getValue().getPedidoCompra()).isSameAs(pedidoCompra);
            assertThat(response.getId()).isNotNull();
            assertThat(response.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
            assertThat(response.getProdutoId()).isEqualTo(input.getProdutoId());
            assertThat(response.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
            assertThat(response.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> compraOrderItemService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao criar item do pedido de compra")
                    .hasRootCauseMessage("O payload do item do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequirePedidoCompraId() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(null, UUID.randomUUID());

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("O ID do pedido de compra é obrigatório.");
            verifyNoInteractions(compraOrderRepository, compraOrderItemRepository);
        }

        @Test
        void shouldRequireProdutoId() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), null);

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .hasRootCauseMessage("O ID do produto é obrigatório.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void shouldRequirePositiveQuantity(int quantidade) {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            input.setQuantidade(quantidade);

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .hasRootCauseMessage("A quantidade deve ser maior que zero.");
        }

        @Test
        void shouldRequireQuantity() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            input.setQuantidade(null);

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .hasRootCauseMessage("A quantidade deve ser maior que zero.");
        }

        @Test
        void shouldRequireUnidadeCusto() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            input.setCustoUnitario(null);

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .hasRootCauseMessage("O custo unidadeário é obrigatório.");
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
            when(compraOrderRepository.findById(input.getCompraOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getCompraOrderId().toString());
            verify(compraOrderItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(compraOrderItemRepository.saveAndFlush(any(PedidoCompraItem.class)))
                    .thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(compraOrderItemRepository.saveAndFlush(any(PedidoCompraItem.class)))
                    .thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> compraOrderItemService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao criar item do pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFields() {
            PedidoCompraItem existing = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
            input.setId(existing.getId());
            input.setQuantidade(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);
            when(compraOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(compraOrderItemRepository.saveAndFlush(existing)).thenReturn(existing);

            ItemPedidoCompraResponseDTO response = compraOrderItemService.update(input);

            assertThat(response.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);
            assertThat(response.getCustoUnitario()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_UNITARIO);
            assertThat(response.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.ATUALIZADO_TOTAL_CUSTO);
            verify(compraOrderRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherOrder() {
            PedidoCompraItem existing = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
            PedidoCompra outros = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
            input.setId(existing.getId());
            input.setCompraOrderId(outros.getId());
            when(compraOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(compraOrderRepository.findById(outros.getId())).thenReturn(Optional.of(outros));
            when(compraOrderItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(compraOrderItemService.update(input).getCompraOrderId()).isEqualTo(outros.getId());
        }

        @Test
        void shouldRequireId() {
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();

            assertThatThrownBy(() -> compraOrderItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar item do pedido de compra")
                    .hasRootCauseMessage("O ID do item do pedido de compra é obrigatório para atualização.");
            assertThatThrownBy(() -> compraOrderItemService.update(null))
                    .hasRootCauseMessage("O ID do item do pedido de compra é obrigatório para atualização.");
            verifyNoInteractions(compraOrderItemRepository);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
            input.setId(UUID.randomUUID());
            when(compraOrderItemRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderItemService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewOrderDoesNotExist() {
            PedidoCompraItem existing = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
            input.setId(existing.getId());
            input.setCompraOrderId(UUID.randomUUID());
            when(compraOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(compraOrderRepository.findById(input.getCompraOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderItemService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(compraOrderItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            PedidoCompraItem existing = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
            ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
            input.setId(existing.getId());
            when(compraOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(compraOrderItemRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> compraOrderItemService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            UUID id = UUID.randomUUID();
            when(compraOrderItemRepository.existsById(id)).thenReturn(true);

            compraOrderItemService.delete(id);

            verify(compraOrderItemRepository).deleteById(id);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(compraOrderItemRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> compraOrderItemService.delete(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
            verify(compraOrderItemRepository, never()).deleteById(any());
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            UUID id = UUID.randomUUID();
            when(compraOrderItemRepository.existsById(id)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("fk")).when(compraOrderItemRepository).deleteById(id);

            assertThatThrownBy(() -> compraOrderItemService.delete(id))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir item do pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindById() {
            PedidoCompraItem existing = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
            when(compraOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(compraOrderItemService.findById(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> compraOrderItemService.findById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldListAll() {
            when(compraOrderItemRepository.findAll()).thenReturn(List.of(PedidoCompraItemFixture.persistedEntity(pedidoCompra)));

            assertThat(compraOrderItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldListByPedidoCompra() {
            when(compraOrderItemRepository.findByPedidoCompraId(pedidoCompra.getId()))
                    .thenReturn(List.of(PedidoCompraItemFixture.persistedEntity(pedidoCompra)));

            assertThat(compraOrderItemService.findByPedidoCompraId(pedidoCompra.getId()))
                    .extracting(ItemPedidoCompraResponseDTO::getCompraOrderId)
                    .containsExactly(pedidoCompra.getId());
        }
    }

    @Nested
    class ReplaceByPedidoCompra {

        @Test
        void shouldDeleteExistingAndInsertNewItens() {
            ItemPedidoCompraInputDTO withId = PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID());
            withId.setId(UUID.randomUUID());
            ArgumentCaptor<List<PedidoCompraItem>> captor = listCaptor();
            when(compraOrderItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<ItemPedidoCompraResponseDTO> response = compraOrderItemService.replaceByPedidoCompra(pedidoCompra, List.of(withId));

            verify(compraOrderItemRepository).deleteByPedidoCompra(pedidoCompra);
            assertThat(captor.getValue()).singleElement().satisfies(item -> {
                assertThat(item.getId()).isNull();
                assertThat(item.getPedidoCompra()).isSameAs(pedidoCompra);
            });
            assertThat(response).hasSize(1);
        }

        @Test
        void shouldOnlyDeleteWhenListIsEmptyOrNull() {
            assertThat(compraOrderItemService.replaceByPedidoCompra(pedidoCompra, List.of())).isEmpty();
            assertThat(compraOrderItemService.replaceByPedidoCompra(pedidoCompra, null)).isEmpty();
            verify(compraOrderItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(compraOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());
            List<ItemPedidoCompraInputDTO> input = List.of(PedidoCompraItemFixture.validoInput(pedidoCompra.getId(), UUID.randomUUID()));

            assertThatThrownBy(() -> compraOrderItemService.replaceByPedidoCompra(pedidoCompra, input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            doThrow(new DataIntegrityViolationException("fk")).when(compraOrderItemRepository).deleteByPedidoCompra(pedidoCompra);

            assertThatThrownBy(() -> compraOrderItemService.replaceByPedidoCompra(pedidoCompra, List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(pedidoCompra.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveAllByPedidoCompra {

        @Test
        void shouldAttachItensToOrderAndSave() {
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            ArgumentCaptor<List<PedidoCompraItem>> captor = listCaptor();
            when(compraOrderItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(null, UUID.randomUUID());
            input.setCustoUnitario(new BigDecimal("2.0000"));

            List<ItemPedidoCompraResponseDTO> response = compraOrderItemService.savePedidoCompraItens(pedidoCompra.getId(), List.of(input));

            assertThat(captor.getValue()).singleElement().satisfies(item -> assertThat(item.getPedidoCompra()).isSameAs(pedidoCompra));
            assertThat(response).singleElement().satisfies(dto -> {
                assertThat(dto.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
                assertThat(dto.getCustoTotal()).isEqualByComparingTo("2000");
            });
        }

        @Test
        void shouldSaveEmptyListWhenInputIsNull() {
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(compraOrderItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(compraOrderItemService.saveAllByPedidoCompra(pedidoCompra.getId(), null)).isEmpty();
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> compraOrderItemService.saveAllByPedidoCompra(id, List.of()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(compraOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> compraOrderItemService.saveAllByPedidoCompra(pedidoCompra.getId(), List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(compraOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> compraOrderItemService.saveAllByPedidoCompra(pedidoCompra.getId(), List.of()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(pedidoCompra.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
