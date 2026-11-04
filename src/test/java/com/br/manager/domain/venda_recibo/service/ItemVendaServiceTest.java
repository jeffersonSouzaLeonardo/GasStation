package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.repository.BicoRepository;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.mapper.ItemVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaItemRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemVendaServiceTest {

    private static final String MENSAGEM_VENDA_BLOQUEADA = "Vendas pagas ou com crédito pendente não podem ser alteradas.";

    @Mock
    private VendaItemRepository vendaItemRepository;
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private BicoRepository bicoRepository;
    @Mock
    private TanqueRepository tanqueRepository;
    @Spy
    private ItemVendaMapper vendaItemMapper = new ItemVendaMapper();

    @InjectMocks
    private ItemVendaService vendaItemService;

    private Venda venda;
    private Produto produto;

    @BeforeEach
    void setUp() {
        venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
        produto = ReciboVendaReferenciaFixture.produto(true);
    }

    private void givenAbertoVendaAndAtivoProduto() {
        when(vendaRepository.findById(venda.getId())).thenReturn(Optional.of(venda));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));
    }

    @SuppressWarnings("unchecked")
    private List<VendaItem> capturedSavedItens() {
        ArgumentCaptor<List<VendaItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(vendaItemRepository).saveAllAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class SaveVendaItens {

        @Test
        void shouldPersistProdutoItemLinkedToVenda() {
            givenAbertoVendaAndAtivoProduto();
            when(vendaItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            List<VendaItemResponseDTO> response = vendaItemService.saveVendaItens(venda.getId(),
                    List.of(VendaItemFixture.produtoInput(produto.getId())));

            assertThat(response).singleElement()
                    .satisfies(item -> assertThat(item.quantidade()).isEqualByComparingTo(VendaItemFixture.QUANTITY))
                    .satisfies(item -> assertThat(item.precoUnitario()).isEqualByComparingTo(VendaItemFixture.PRECO_UNITARIO))
                    .satisfies(item -> assertThat(item.produtoId()).isEqualTo(produto.getId()));
            assertThat(capturedSavedItens()).singleElement()
                    .satisfies(item -> assertThat(item.getVenda()).isSameAs(venda));
        }

        @Test
        void shouldDefaultDescontoToZeroWhenNotInformed() {
            givenAbertoVendaAndAtivoProduto();
            VendaItemInputDTO input = VendaItemFixture.produtoInput(produto.getId());
            input.setValorDesconto(null);

            vendaItemService.saveVendaItens(venda.getId(), List.of(input));

            assertThat(capturedSavedItens().get(0).getValorDesconto()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldCalculateCombustivelQuantityFromMedidorLeiturasAndUseBicoTanque() {
            givenAbertoVendaAndAtivoProduto();
            Tanque tanque = ReciboVendaReferenciaFixture.tanque(produto.getId());
            Bico bico = ReciboVendaReferenciaFixture.bico(UUID.randomUUID(), tanque.getId(), produto.getId());
            when(bicoRepository.findById(bico.getId())).thenReturn(Optional.of(bico));
            when(tanqueRepository.findById(tanque.getId())).thenReturn(Optional.of(tanque));

            vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.combustivelInput(produto.getId(), bico.getId())));

            VendaItem saved = capturedSavedItens().get(0);
            assertThat(saved.getQuantidade()).isEqualByComparingTo(VendaItemFixture.COMBUSTIVEL_QUANTITY);
            assertThat(saved.getTanqueId()).isEqualTo(tanque.getId());
            assertThat(saved.getBicoId()).isEqualTo(bico.getId());
        }

        @Test
        void shouldThrowNotFoundWhenVendaDoesNotExist() {
            UUID vendaId = UUID.randomUUID();
            when(vendaRepository.findById(vendaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(vendaId, List.of(VendaItemFixture.produtoInput(produto.getId()))))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(vendaId.toString());
        }

        @Test
        void shouldRejectItensForPagoVenda() {
            venda.setStatus(VendaSituacao.PAGO);
            when(vendaRepository.findById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.produtoInput(produto.getId()))))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(MENSAGEM_VENDA_BLOQUEADA);
            verifyNoInteractions(produtoRepository, vendaItemRepository);
        }

        @Test
        void shouldRejectItensForVendaWithPendenteCredito() {
            venda.setStatus(VendaSituacao.CREDITO_PENDENTE);
            when(vendaRepository.findById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.produtoInput(produto.getId()))))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(MENSAGEM_VENDA_BLOQUEADA);
        }

        @Test
        void shouldRejectDuplicatedLinhaNumbers() {
            when(vendaRepository.findById(venda.getId())).thenReturn(Optional.of(venda));
            List<VendaItemInputDTO> inputs = List.of(
                    VendaItemFixture.produtoInput(produto.getId()),
                    VendaItemFixture.produtoInput(produto.getId()));

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), inputs))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Os números de linha dos itens da venda devem ser únicos.");
            verifyNoInteractions(vendaItemRepository);
        }

        @Test
        void shouldPersistItensWithDistinctLinhaNumbers() {
            givenAbertoVendaAndAtivoProduto();
            List<VendaItemInputDTO> inputs = List.of(
                    VendaItemFixture.produtoInput(produto.getId(), VendaItemFixture.LINHA_NUMERO),
                    VendaItemFixture.produtoInput(produto.getId(), VendaItemFixture.SECOND_LINHA_NUMERO));

            vendaItemService.saveVendaItens(venda.getId(), inputs);

            assertThat(capturedSavedItens())
                    .extracting(VendaItem::getLinhaNumero)
                    .containsExactly(VendaItemFixture.LINHA_NUMERO, VendaItemFixture.SECOND_LINHA_NUMERO);
        }

        @Test
        void shouldThrowNotFoundWhenProdutoDoesNotExist() {
            when(vendaRepository.findById(venda.getId())).thenReturn(Optional.of(venda));
            when(produtoRepository.findById(produto.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.produtoInput(produto.getId()))))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(produto.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenProdutoIsInativo() {
            produto.setAtivo(false);
            givenAbertoVendaAndAtivoProduto();

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.produtoInput(produto.getId()))))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(vendaItemRepository, never()).saveAllAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenBicoDoesNotExist() {
            givenAbertoVendaAndAtivoProduto();
            UUID bicoId = UUID.randomUUID();
            when(bicoRepository.findById(bicoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.combustivelInput(produto.getId(), bicoId))))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(bicoId.toString());
        }

        @Test
        void shouldThrowNotFoundWhenBicoTanqueDoesNotExist() {
            givenAbertoVendaAndAtivoProduto();
            Bico bico = ReciboVendaReferenciaFixture.bico(UUID.randomUUID(), UUID.randomUUID(), produto.getId());
            when(bicoRepository.findById(bico.getId())).thenReturn(Optional.of(bico));
            when(tanqueRepository.findById(bico.getTanqueId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(VendaItemFixture.combustivelInput(produto.getId(), bico.getId()))))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(bico.getTanqueId().toString());
        }

        @Test
        void shouldRejectCombustivelItemWithoutMedidorLeituras() {
            Bico bico = givenCombustivelBico();
            VendaItemInputDTO input = VendaItemFixture.combustivelInput(produto.getId(), bico.getId());
            input.setMedidorFinal(null);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Itens de combustível exigem leitura inicial e final do medidor.");
        }

        @Test
        void shouldRejectCombustivelItemWhenMedidorEndIsNotGreaterThanStart() {
            Bico bico = givenCombustivelBico();
            VendaItemInputDTO input = VendaItemFixture.combustivelInput(produto.getId(), bico.getId());
            input.setMedidorFinal(VendaItemFixture.MEDIDOR_START);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A leitura final deve ser maior que a leitura inicial.");
        }

        @Test
        void shouldRejectProdutoItemWithoutPositiveQuantity() {
            givenAbertoVendaAndAtivoProduto();
            VendaItemInputDTO input = VendaItemFixture.produtoInput(produto.getId());
            input.setQuantidade(BigDecimal.ZERO);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A quantidade do item deve ser maior que zero.");
        }

        @Test
        void shouldRejectProdutoItemWithNullQuantity() {
            givenAbertoVendaAndAtivoProduto();
            VendaItemInputDTO input = VendaItemFixture.produtoInput(produto.getId());
            input.setQuantidade(null);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A quantidade do item deve ser maior que zero.");
        }

        @Test
        void shouldRejectItemWithoutPositiveUnidadePreco() {
            givenAbertoVendaAndAtivoProduto();
            VendaItemInputDTO input = VendaItemFixture.produtoInput(produto.getId());
            input.setPrecoUnitario(BigDecimal.ZERO);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O preço unidadeário deve ser maior que zero.");
            verify(vendaItemRepository, never()).saveAllAndFlush(any());
        }

        @Test
        void shouldRejectItemWithNullUnidadePreco() {
            givenAbertoVendaAndAtivoProduto();
            VendaItemInputDTO input = VendaItemFixture.produtoInput(produto.getId());
            input.setPrecoUnitario(null);

            assertThatThrownBy(() -> vendaItemService.saveVendaItens(venda.getId(), List.of(input)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O preço unidadeário deve ser maior que zero.");
        }

        private Bico givenCombustivelBico() {
            givenAbertoVendaAndAtivoProduto();
            Tanque tanque = ReciboVendaReferenciaFixture.tanque(produto.getId());
            Bico bico = ReciboVendaReferenciaFixture.bico(UUID.randomUUID(), tanque.getId(), produto.getId());
            when(bicoRepository.findById(bico.getId())).thenReturn(Optional.of(bico));
            when(tanqueRepository.findById(tanque.getId())).thenReturn(Optional.of(tanque));
            return bico;
        }
    }

    @Nested
    class FindByVenda {

        @Test
        void shouldReturnItensPedidoByLinhaNumero() {
            VendaItem item = VendaItemFixture.persistedEntity(produto.getId());
            when(vendaItemRepository.findByVendaIdOrderByLinhaNumero(venda.getId())).thenReturn(List.of(item));

            assertThat(vendaItemService.findByVenda(venda.getId()))
                    .extracting(VendaItemResponseDTO::id)
                    .containsExactly(item.getId());
        }

        @Test
        void shouldReturnEmptyListWhenVendaHasNoItens() {
            when(vendaItemRepository.findByVendaIdOrderByLinhaNumero(venda.getId())).thenReturn(List.of());

            assertThat(vendaItemService.findByVenda(venda.getId())).isEmpty();
        }
    }
}
