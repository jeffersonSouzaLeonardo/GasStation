package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.mapper.RecebimentoMercadoriaMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemRecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.RecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoViolacoes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
class RecebimentoMercadoriaServiceTest {

    @Mock
    private RecebimentoMercadoriaRepository mercadoriaReciboRepository;

    @Mock
    private ItemRecebimentoMercadoriaRepository mercadoriaReciboItemRepository;

    @Mock
    private ItemRecebimentoMercadoriaService mercadoriaReciboItemService;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private PedidoCompraRepository compraOrderRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private RecebimentoMercadoriaMapper mercadoriaReciboMapper = new RecebimentoMercadoriaMapper();

    @InjectMocks
    private RecebimentoMercadoriaService mercadoriaReciboService;

    private final Fornecedor fornecedor = AquisicaoPrecoReferenciaFixture.ativoFornecedor();
    private final Usuario receiver = AquisicaoPrecoReferenciaFixture.ativoUsuario();

    private RecebimentoMercadoriaInputDTO validoInput() {
        return RecebimentoMercadoriaFixture.validoInput(fornecedor.getId(), receiver.getId(),
                List.of(RecebimentoMercadoriaItemFixture.validoInput(null, UUID.randomUUID(), null)));
    }

    private void stubReferencias() {
        when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
        when(usuarioRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
    }

    private void stubSaveAssigningId() {
        when(mercadoriaReciboRepository.saveAndFlush(any(RecebimentoMercadoria.class))).thenAnswer(invocation -> {
            RecebimentoMercadoria recibo = invocation.getArgument(0);
            assertThat(recibo.getId()).isNull();
            recibo.setId(UUID.randomUUID());
            return recibo;
        });
    }

    private RecebimentoMercadoria stubExisting() {
        RecebimentoMercadoria existing = RecebimentoMercadoriaFixture.persistedEntity(fornecedor, receiver);
        when(mercadoriaReciboRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        return existing;
    }

    private RecebimentoMercadoriaInputDTO updateInput(UUID id) {
        RecebimentoMercadoriaInputDTO input = new RecebimentoMercadoriaInputDTO();
        input.setId(id);
        return input;
    }

    @Nested
    class Create {

        @Test
        void shouldCreateReciboWithReferenciasAndItens() {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setId(UUID.randomUUID());
            stubReferencias();
            ArgumentCaptor<RecebimentoMercadoria> captor = ArgumentCaptor.forClass(RecebimentoMercadoria.class);
            stubSaveAssigningId();
            when(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(any()))
                    .thenAnswer(invocation -> {
                        RecebimentoMercadoria owner = new RecebimentoMercadoria();
                        owner.setId(invocation.getArgument(0));
                        return List.of(RecebimentoMercadoriaItemFixture.persistedEntity(owner));
                    });

            RecebimentoMercadoriaResponseDTO response = mercadoriaReciboService.create(input);

            verify(mercadoriaReciboRepository).saveAndFlush(captor.capture());
            RecebimentoMercadoria saved = captor.getValue();
            assertThat(saved.getFornecedor()).isSameAs(fornecedor);
            assertThat(saved.getRecebidoPor()).isSameAs(receiver);
            assertThat(saved.getPedidoCompra()).isNull();
            verify(mercadoriaReciboItemService).saveRecebimentoMercadoriaItens(saved.getId(), input.getItens());
            assertThat(response.getId()).isEqualTo(saved.getId());
            assertThat(response.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.RECEBIDO);
            assertThat(response.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM);
            assertThat(response.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA);
            assertThat(response.getValorFrete()).isEqualByComparingTo(RecebimentoMercadoriaFixture.VALOR_FRETE);
            assertThat(response.getItens()).singleElement()
                    .satisfies(item -> assertThat(item.getMercadoriaReciboId()).isEqualTo(saved.getId()));
        }

        @Test
        void shouldVinculoPedidoCompraWhenInformed() {
            PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.SENT);
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setCompraOrderId(pedidoCompra.getId());
            stubReferencias();
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            stubSaveAssigningId();

            assertThat(mercadoriaReciboService.create(input).getCompraOrderId()).isEqualTo(pedidoCompra.getId());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "  ")
        void shouldDefaultSituacaoToDraftAndRecebidoEmToNow(String status) {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setStatus(status);
            input.setRecebidoEm(null);
            stubReferencias();
            stubSaveAssigningId();
            LocalDateTime before = LocalDateTime.now();

            RecebimentoMercadoriaResponseDTO response = mercadoriaReciboService.create(input);

            assertThat(response.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.DRAFT);
            assertThat(response.getRecebidoEm()).isBetween(before, LocalDateTime.now());
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> mercadoriaReciboService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O payload do recebimento de mercadorias é obrigatório.");
        }

        @Test
        void shouldRequireFornecedor() {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setFornecedorId(null);

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do fornecedor é obrigatório.");
        }

        @Test
        void shouldRequireReceiver() {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setRecebidoPor(null);

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .hasMessage("O usuário responsável pelo recebimento é obrigatório.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void shouldRequireNotaNumero(String numeroNota) {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setNumeroNota(numeroNota);

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .hasMessage("O número da nota fiscal é obrigatório.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void shouldRequireNotaKey(String chaveNota) {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setChaveNota(chaveNota);

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .hasMessage("A chave da nota fiscal é obrigatória.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldRequireItens(List<ItemRecebimentoMercadoriaInputDTO> itens) {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setItens(itens);

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .hasMessage("O recebimento de mercadorias deve conter pelo menos um item.");
            verifyNoInteractions(mercadoriaReciboRepository);
        }

        @Test
        void shouldThrowNotFoundWhenFornecedorDoesNotExist() {
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mercadoriaReciboService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(fornecedor.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenFornecedorIsInativo() {
            Fornecedor inativo = AquisicaoPrecoReferenciaFixture.inativoFornecedor();
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setFornecedorId(inativo.getId());
            when(fornecedorRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> mercadoriaReciboService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenPedidoCompraDoesNotExist() {
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setCompraOrderId(UUID.randomUUID());
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(compraOrderRepository.findById(input.getCompraOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mercadoriaReciboService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getCompraOrderId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenReceiverDoesNotExist() {
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(receiver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mercadoriaReciboService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(receiver.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenReceiverIsInativo() {
            Usuario inativo = AquisicaoPrecoReferenciaFixture.inativoUsuario();
            RecebimentoMercadoriaInputDTO input = validoInput();
            input.setRecebidoPor(inativo.getId());
            when(fornecedorRepository.findById(fornecedor.getId())).thenReturn(Optional.of(fornecedor));
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> mercadoriaReciboService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            stubReferencias();
            when(mercadoriaReciboRepository.saveAndFlush(any(RecebimentoMercadoria.class)))
                    .thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            stubReferencias();
            when(mercadoriaReciboRepository.saveAndFlush(any(RecebimentoMercadoria.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> mercadoriaReciboService.create(validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar recebimento de mercadorias " + RecebimentoMercadoriaFixture.NUMERO_NOTA)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateFieldsReferenciasAndReplaceItens() {
            RecebimentoMercadoria existing = stubExisting();
            Fornecedor newFornecedor = AquisicaoPrecoReferenciaFixture.ativoFornecedor();
            Usuario newReceiver = AquisicaoPrecoReferenciaFixture.ativoUsuario();
            PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.SENT);
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setFornecedorId(newFornecedor.getId());
            input.setRecebidoPor(newReceiver.getId());
            input.setCompraOrderId(pedidoCompra.getId());
            input.setNumeroNota(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
            input.setStatus(RecebimentoMercadoria.Situacao.PARTIALLY_RECEBIDO);
            input.setRecebidoEm(RecebimentoMercadoriaFixture.RECEBIDO_EM.plusHours(2));
            List<ItemRecebimentoMercadoriaInputDTO> itens = List.of(RecebimentoMercadoriaItemFixture.validoInput(existing.getId(), UUID.randomUUID(), null));
            input.setItens(itens);
            when(fornecedorRepository.findById(newFornecedor.getId())).thenReturn(Optional.of(newFornecedor));
            when(usuarioRepository.findById(newReceiver.getId())).thenReturn(Optional.of(newReceiver));
            when(compraOrderRepository.findById(pedidoCompra.getId())).thenReturn(Optional.of(pedidoCompra));
            when(mercadoriaReciboRepository.saveAndFlush(existing)).thenReturn(existing);
            when(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(existing.getId()))
                    .thenReturn(List.of(RecebimentoMercadoriaItemFixture.persistedEntity(existing)));

            RecebimentoMercadoriaResponseDTO response = mercadoriaReciboService.update(input);

            assertThat(response.getFornecedorId()).isEqualTo(newFornecedor.getId());
            assertThat(response.getRecebidoPor()).isEqualTo(newReceiver.getId());
            assertThat(response.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
            assertThat(response.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
            assertThat(response.getChaveNota()).isEqualTo(RecebimentoMercadoriaFixture.CHAVE_NOTA);
            assertThat(response.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.PARTIALLY_RECEBIDO);
            assertThat(response.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM.plusHours(2));
            assertThat(response.getItens()).hasSize(1);
            verify(mercadoriaReciboItemService).replaceByRecebimentoMercadoria(existing.getId(), itens);
        }

        @Test
        void shouldKeepReferenciasAndItensWhenNotInformed() {
            RecebimentoMercadoria existing = stubExisting();
            when(mercadoriaReciboRepository.saveAndFlush(existing)).thenReturn(existing);

            RecebimentoMercadoriaResponseDTO response = mercadoriaReciboService.update(updateInput(existing.getId()));

            assertThat(response.getFornecedorId()).isEqualTo(fornecedor.getId());
            assertThat(response.getRecebidoPor()).isEqualTo(receiver.getId());
            assertThat(response.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.RECEBIDO);
            verifyNoInteractions(fornecedorRepository, usuarioRepository, compraOrderRepository, mercadoriaReciboItemService);
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> mercadoriaReciboService.update(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O payload do recebimento de mercadorias é obrigatório.");
        }

        @Test
        void shouldRequireId() {
            assertThatThrownBy(() -> mercadoriaReciboService.update(new RecebimentoMercadoriaInputDTO()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do recebimento de mercadorias é obrigatório para atualização.");
            verifyNoInteractions(mercadoriaReciboRepository);
        }

        @Test
        void shouldThrowNotFoundWhenReciboDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(mercadoriaReciboRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mercadoriaReciboService.update(updateInput(id)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewFornecedorIsInativo() {
            RecebimentoMercadoria existing = stubExisting();
            Fornecedor inativo = AquisicaoPrecoReferenciaFixture.inativoFornecedor();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setFornecedorId(inativo.getId());
            when(fornecedorRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> mercadoriaReciboService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenNewFornecedorDoesNotExist() {
            RecebimentoMercadoria existing = stubExisting();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setFornecedorId(UUID.randomUUID());

            assertThatThrownBy(() -> mercadoriaReciboService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewPedidoCompraDoesNotExist() {
            RecebimentoMercadoria existing = stubExisting();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setCompraOrderId(UUID.randomUUID());

            assertThatThrownBy(() -> mercadoriaReciboService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewReceiverIsInativo() {
            RecebimentoMercadoria existing = stubExisting();
            Usuario inativo = AquisicaoPrecoReferenciaFixture.inativoUsuario();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setRecebidoPor(inativo.getId());
            when(usuarioRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> mercadoriaReciboService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewReceiverDoesNotExist() {
            RecebimentoMercadoria existing = stubExisting();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setRecebidoPor(UUID.randomUUID());

            assertThatThrownBy(() -> mercadoriaReciboService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            RecebimentoMercadoria existing = stubExisting();
            when(mercadoriaReciboRepository.saveAndFlush(existing)).thenThrow(AquisicaoPrecoViolacoes.constraintViolationException());

            assertThatThrownBy(() -> mercadoriaReciboService.update(updateInput(existing.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            RecebimentoMercadoria existing = stubExisting();
            RecebimentoMercadoriaInputDTO input = updateInput(existing.getId());
            input.setNumeroNota(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
            when(mercadoriaReciboRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> mercadoriaReciboService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar recebimento de mercadorias " + RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAll() {
            when(mercadoriaReciboRepository.findAll()).thenReturn(List.of(RecebimentoMercadoriaFixture.persistedEntity()));

            assertThat(mercadoriaReciboService.findAll()).hasSize(1);
        }

        @Test
        void shouldListByFornecedor() {
            when(mercadoriaReciboRepository.findByFornecedorId(fornecedor.getId()))
                    .thenReturn(List.of(RecebimentoMercadoriaFixture.persistedEntity(fornecedor, receiver)));

            assertThat(mercadoriaReciboService.findByFornecedor(fornecedor.getId()))
                    .extracting(RecebimentoMercadoriaResponseDTO::getFornecedorId)
                    .containsExactly(fornecedor.getId());
        }

        @Test
        void shouldListByPedidoCompra() {
            UUID compraOrderId = UUID.randomUUID();
            when(mercadoriaReciboRepository.findByPedidoCompraId(compraOrderId)).thenReturn(List.of());

            assertThat(mercadoriaReciboService.findByPedidoCompra(compraOrderId)).isEmpty();
        }

        @Test
        void shouldFindById() {
            RecebimentoMercadoria existing = stubExisting();

            assertThat(mercadoriaReciboService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingRecibo() {
            RecebimentoMercadoria existing = stubExisting();

            mercadoriaReciboService.delete(existing.getId());

            verify(mercadoriaReciboRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenReciboDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> mercadoriaReciboService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(mercadoriaReciboRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoExcecaoNegocio() {
            RecebimentoMercadoria existing = stubExisting();
            doThrow(AquisicaoPrecoViolacoes.constraintViolationException()).when(mercadoriaReciboRepository).delete(existing);

            assertThatThrownBy(() -> mercadoriaReciboService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AquisicaoPrecoViolacoes.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            RecebimentoMercadoria existing = stubExisting();
            doThrow(new DataIntegrityViolationException("fk")).when(mercadoriaReciboRepository).delete(existing);

            assertThatThrownBy(() -> mercadoriaReciboService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
