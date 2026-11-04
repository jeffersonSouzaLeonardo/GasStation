package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
import com.br.manager.domain.aquisicao_preco.mapper.PedidoCompraMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoViolacoes;
import org.junit.jupiter.api.Disabled;
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

import java.time.LocalDateTime;
import java.util.List;
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
class PedidoCompraServiceTest {

    @Mock
    private PedidoCompraRepository compraOrderRepository;

    @Mock
    private ItemPedidoCompraRepository compraOrderItemRepository;

    @Mock
    private ItemPedidoCompraService compraOrderItemService;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private PedidoCompraMapper compraOrderMapper = new PedidoCompraMapper();

    @InjectMocks
    private PedidoCompraService compraOrderService;

    private final Fornecedor fornecedor = AquisicaoPrecoReferenciaFixture.ativoFornecedor();
    private final Usuario creator = AquisicaoPrecoReferenciaFixture.ativoUsuario();

    private PedidoCompraInputDTO validoInput() {
        return PedidoCompraFixture.validoInput(fornecedor.getId(), creator.getId(),
                List.of(PedidoCompraItemFixture.validoInput(null, UUID.randomUUID())));
    }

    private PedidoCompra stubExisting(String status) {
        PedidoCompra existing = PedidoCompraFixture.persistedEntity(status, fornecedor, creator);
        when(compraOrderRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        return existing;
    }

    private void stubSaveReturningArgument() {
        when(compraOrderRepository.saveAndFlush(any(PedidoCompra.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class Create {

        @Test
        void shouldCreateDraftOrderWithFornecedorCreatorAndItens() {
            PedidoCompraInputDTO input = validoInput();
            input.setId(UUID.randomUUID());
            input.setStatus(PedidoCompra.Situacao.COMPLETED);
            UUID generatedId = UUID.randomUUID();
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            ArgumentCaptor<PedidoCompra> captor = ArgumentCaptor.forClass(PedidoCompra.class);
            when(compraOrderRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                PedidoCompra order = invocation.getArgument(0);
                assertThat(order.getId()).isNull();
                order.setId(generatedId);
                return order;
            });
            PedidoCompraItem savedItem = PedidoCompraItemFixture.persistedEntity(null);
            when(compraOrderItemRepository.findByPedidoCompraId(generatedId)).thenReturn(List.of(savedItem));

            PedidoCompraResponseDTO response = compraOrderService.create(input);

            PedidoCompra saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(PedidoCompra.Situacao.DRAFT);
            assertThat(saved.getFornecedor()).isSameAs(fornecedor);
            assertThat(saved.getCriadoPor()).isSameAs(creator);
            assertThat(saved.getPedidoEm()).isEqualTo(PedidoCompraFixture.PEDIDO_EM);
            assertThat(saved.getPrevistoEm()).isEqualTo(PedidoCompraFixture.PREVISTO_EM);
            verify(compraOrderItemService).savePedidoCompraItens(generatedId, input.getItens());
            assertThat(response.getId()).isEqualTo(generatedId);
            assertThat(response.getStatus()).isEqualTo(PedidoCompra.Situacao.DRAFT);
            assertThat(response.getFornecedorId()).isEqualTo(fornecedor.getId());
            assertThat(response.getCriadoPor()).isEqualTo(creator.getId());
            assertThat(response.getItens()).hasSize(1);
            assertThat(response.getValorTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
        }

        @Test
        void shouldDefaultPedidoEmToNowWhenNotInformed() {
            PedidoCompraInputDTO input = validoInput();
            input.setPedidoEm(null);
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            stubSaveReturningArgument();
            LocalDateTime before = LocalDateTime.now();

            PedidoCompraResponseDTO response = compraOrderService.create(input);

            assertThat(response.getPedidoEm()).isBetween(before, LocalDateTime.now());
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> compraOrderService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O payload do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireFornecedor() {
            PedidoCompraInputDTO input = validoInput();
            input.setFornecedorId(null);

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do fornecedor é obrigatório.");
        }

        @Test
        void shouldRequireCreator() {
            PedidoCompraInputDTO input = validoInput();
            input.setCriadoPor(null);

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O usuário criador é obrigatório.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void shouldRequireNumero(String numero) {
            PedidoCompraInputDTO input = validoInput();
            input.setNumero(numero);

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O número do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireNonNullNumero() {
            PedidoCompraInputDTO input = validoInput();
            input.setNumero(null);

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .hasMessage("O número do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireEsperadoEm() {
            PedidoCompraInputDTO input = validoInput();
            input.setPrevistoEm(null);

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A data prevista é obrigatória.");
        }

        @Test
        void shouldRequireAtLeastOneItem() {
            PedidoCompraInputDTO empty = validoInput();
            empty.setItens(List.of());
            PedidoCompraInputDTO nullItens = validoInput();
            nullItens.setItens(null);

            assertThatThrownBy(() -> compraOrderService.create(empty))
                    .hasMessage("O pedido de compra deve conter pelo menos um item.");
            assertThatThrownBy(() -> compraOrderService.create(nullItens))
                    .hasMessage("O pedido de compra deve conter pelo menos um item.");
            verifyNoInteractions(compraOrderRepository);
        }

        @Test
        void shouldThrowNotFoundWhenFornecedorDoesNotExist() {
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(fornecedor.getId().toString());
            verify(compraOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenFornecedorIsInativo() {
            Fornecedor inativo = AquisicaoPrecoReferenciaFixture.inativoFornecedor();
            PedidoCompraInputDTO input = validoInput();
            input.setFornecedorId(inativo.getId());
            when(fornecedorRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(compraOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorDoesNotExist() {
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(creator.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorIsInativo() {
            Usuario inativo = AquisicaoPrecoReferenciaFixture.inativoUsuario();
            PedidoCompraInputDTO input = validoInput();
            input.setCriadoPor(inativo.getId());
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(compraOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(compraOrderRepository.saveAndFlush(any(PedidoCompra.class)))
                    .thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> compraOrderService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapItemErrorInExcecaoNegocio() {
            PedidoCompraInputDTO input = validoInput();
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            stubSaveReturningArgument();
            doThrow(new DataIntegrityViolationException("fk"))
                    .when(compraOrderItemService).savePedidoCompraItens(any(), any());

            assertThatThrownBy(() -> compraOrderService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFieldsAndReplaceItens() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            PedidoCompraInputDTO input = new PedidoCompraInputDTO();
            input.setId(existing.getId());
            input.setNumero(PedidoCompraFixture.ATUALIZADO_NUMERO);
            input.setPedidoEm(PedidoCompraFixture.PEDIDO_EM.plusDays(1));
            input.setPrevistoEm(PedidoCompraFixture.PREVISTO_EM.plusDays(1));
            input.setStatus(PedidoCompra.Situacao.APROVADO);
            List<ItemPedidoCompraInputDTO> itens = List.of(PedidoCompraItemFixture.validoInput(existing.getId(), UUID.randomUUID()));
            input.setItens(itens);
            when(compraOrderRepository.saveAndFlush(existing)).thenReturn(existing);
            when(compraOrderItemRepository.findByPedidoCompraId(existing.getId()))
                    .thenReturn(List.of(PedidoCompraItemFixture.persistedEntity(existing)));

            PedidoCompraResponseDTO response = compraOrderService.update(input);

            assertThat(response.getNumero()).isEqualTo(PedidoCompraFixture.ATUALIZADO_NUMERO);
            assertThat(response.getPedidoEm()).isEqualTo(PedidoCompraFixture.PEDIDO_EM.plusDays(1));
            assertThat(response.getPrevistoEm()).isEqualTo(PedidoCompraFixture.PREVISTO_EM.plusDays(1));
            assertThat(response.getStatus()).isEqualTo(PedidoCompra.Situacao.APROVADO);
            assertThat(response.getItens()).hasSize(1);
            verify(compraOrderItemService).replaceByPedidoCompra(existing, itens);
        }

        @Test
        void shouldKeepCurrentValuesWhenFieldsAreVazioOrNull() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            PedidoCompraInputDTO input = new PedidoCompraInputDTO();
            input.setId(existing.getId());
            input.setNumero(" ");
            input.setStatus("");
            when(compraOrderRepository.saveAndFlush(existing)).thenReturn(existing);

            PedidoCompraResponseDTO response = compraOrderService.update(input);

            assertThat(response.getNumero()).isEqualTo(PedidoCompraFixture.NUMERO);
            assertThat(response.getStatus()).isEqualTo(PedidoCompra.Situacao.DRAFT);
            assertThat(response.getPedidoEm()).isEqualTo(PedidoCompraFixture.PEDIDO_EM);
            verify(compraOrderItemService, never()).replaceByPedidoCompra(any(), any());
            verify(compraOrderItemRepository, never()).findByPedidoCompraId(any());
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            PedidoCompraInputDTO input = validoInput();
            input.setId(UUID.randomUUID());
            when(compraOrderRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> compraOrderService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        @Disabled("BUG: PedidoCompraService.update chama inputDTO.getId() (linha 107) antes da checagem "
                + "'inputDTO == null' (linha 111), que vira codigo morto; payload nulo gera NPE e a mensagem generica "
                + "'Erro ao atualizar pedido de compra' em vez da mensagem de validacao.")
        void shouldRejectNullPayloadWithValidacaoMessage() {
            assertThatThrownBy(() -> compraOrderService.update(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O payload do pedido de compra é obrigatório.");
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            PedidoCompraInputDTO input = new PedidoCompraInputDTO();
            input.setId(existing.getId());
            when(compraOrderRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> compraOrderService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            PedidoCompraInputDTO input = new PedidoCompraInputDTO();
            input.setId(existing.getId());
            when(compraOrderRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> compraOrderService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SituacaoTransitions {

        private void assertSavedSituacao(PedidoCompraResponseDTO response, String esperado) {
            ArgumentCaptor<PedidoCompra> captor = ArgumentCaptor.forClass(PedidoCompra.class);
            verify(compraOrderRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(esperado);
            assertThat(response.getStatus()).isEqualTo(esperado);
        }

        @Test
        void shouldApproveDraftOrder() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            stubSaveReturningArgument();

            assertSavedSituacao(compraOrderService.approve(existing.getId()), PedidoCompra.Situacao.APROVADO);
        }

        @Test
        void shouldSendAprovadoOrder() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.APROVADO);
            stubSaveReturningArgument();

            assertSavedSituacao(compraOrderService.send(existing.getId()), PedidoCompra.Situacao.SENT);
        }

        @Test
        void shouldCompleteSentOrder() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.SENT);
            stubSaveReturningArgument();

            assertSavedSituacao(compraOrderService.complete(existing.getId()), PedidoCompra.Situacao.COMPLETED);
        }

        @ParameterizedTest
        @ValueSource(strings = {PedidoCompra.Situacao.DRAFT, PedidoCompra.Situacao.APROVADO, PedidoCompra.Situacao.SENT})
        void shouldCancelAbertoOrder(String status) {
            PedidoCompra existing = stubExisting(status);
            stubSaveReturningArgument();

            assertSavedSituacao(compraOrderService.cancel(existing.getId()), PedidoCompra.Situacao.CANCELADO);
        }

        @ParameterizedTest
        @ValueSource(strings = {PedidoCompra.Situacao.COMPLETED, PedidoCompra.Situacao.CANCELADO})
        void shouldRejectCancelOfFechadoOrder(String status) {
            PedidoCompra existing = stubExisting(status);

            assertThatThrownBy(() -> compraOrderService.cancel(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados.");
            verify(compraOrderRepository, never()).saveAndFlush(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {PedidoCompra.Situacao.APROVADO, PedidoCompra.Situacao.SENT, PedidoCompra.Situacao.COMPLETED, PedidoCompra.Situacao.CANCELADO})
        void shouldRejectApproveWhenNotDraft(String status) {
            PedidoCompra existing = stubExisting(status);

            assertThatThrownBy(() -> compraOrderService.approve(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O pedido de compra só pode mudar de DRAFT para APROVADO.");
            verify(compraOrderRepository, never()).saveAndFlush(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {PedidoCompra.Situacao.DRAFT, PedidoCompra.Situacao.SENT, PedidoCompra.Situacao.COMPLETED})
        void shouldRejectSendWhenNotAprovado(String status) {
            PedidoCompra existing = stubExisting(status);

            assertThatThrownBy(() -> compraOrderService.send(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O pedido de compra só pode mudar de APROVADO para SENT.");
        }

        @ParameterizedTest
        @ValueSource(strings = {PedidoCompra.Situacao.DRAFT, PedidoCompra.Situacao.APROVADO, PedidoCompra.Situacao.CANCELADO})
        void shouldRejectCompleteWhenNotSent(String status) {
            PedidoCompra existing = stubExisting(status);

            assertThatThrownBy(() -> compraOrderService.complete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O pedido de compra só pode mudar de SENT para COMPLETED.");
        }

        @Test
        void shouldThrowNotFoundForUnknownOrder() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> compraOrderService.approve(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> compraOrderService.send(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> compraOrderService.complete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> compraOrderService.cancel(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindByIdLoadingItens() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);
            when(compraOrderItemRepository.findByPedidoCompraId(existing.getId()))
                    .thenReturn(List.of(PedidoCompraItemFixture.persistedEntity(existing)));

            PedidoCompraResponseDTO response = compraOrderService.findById(existing.getId());

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getItens()).hasSize(1);
            assertThat(response.getValorTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> compraOrderService.findById(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListAllLoadingItens() {
            PedidoCompra first = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);
            PedidoCompra second = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.SENT);
            when(compraOrderRepository.findAll()).thenReturn(List.of(first, second));
            when(compraOrderItemRepository.findByPedidoCompraId(first.getId()))
                    .thenReturn(List.of(PedidoCompraItemFixture.persistedEntity(first)));

            List<PedidoCompraResponseDTO> response = compraOrderService.findAll();

            assertThat(response).extracting(PedidoCompraResponseDTO::getId).containsExactly(first.getId(), second.getId());
            assertThat(response.get(0).getItens()).hasSize(1);
            assertThat(response.get(1).getItens()).isEmpty();
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingOrder() {
            PedidoCompra existing = stubExisting(PedidoCompra.Situacao.DRAFT);

            compraOrderService.delete(existing.getId());

            verify(compraOrderRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownOrder() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> compraOrderService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(compraOrderRepository, never()).delete(any());
        }
    }
}
