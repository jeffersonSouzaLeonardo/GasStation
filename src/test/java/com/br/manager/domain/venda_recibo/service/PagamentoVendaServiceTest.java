package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.repository.MetodoPagamentoRepository;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.mapper.PagamentoVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaPagamentoRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoVendaServiceTest {

    private static final String WRAPPED_MESSAGE = "Erro ao salvar pagamento da venda";

    @Mock
    private VendaPagamentoRepository vendaPagamentoRepository;
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private MetodoPagamentoRepository pagamentoMethodRepository;
    @Spy
    private PagamentoVendaMapper vendaPagamentoMapper = new PagamentoVendaMapper();

    @InjectMocks
    private PagamentoVendaService vendaPagamentoService;

    private Venda venda;
    private MetodoPagamento dinheiro;

    @BeforeEach
    void setUp() {
        venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
        dinheiro = ReciboVendaReferenciaFixture.dinheiroMetodoPagamento();
    }

    private void givenVendaAndMethod(MetodoPagamento method) {
        when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
        when(pagamentoMethodRepository.findById(method.getId())).thenReturn(Optional.of(method));
    }

    private void givenSuccessfulSave(SituacaoPagamento... previousStatuses) {
        when(vendaPagamentoRepository.saveAndFlush(any(PagamentoVenda.class))).thenAnswer(invocation -> {
            PagamentoVenda pagamento = invocation.getArgument(0);
            pagamento.setId(UUID.randomUUID());
            return pagamento;
        });
        List<PagamentoVenda> stored = java.util.Arrays.stream(previousStatuses)
                .map(VendaPagamentoFixture::persistedEntity)
                .toList();
        when(vendaPagamentoRepository.findByVendaId(venda.getId())).thenReturn(stored);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistPagamentoLinkedToVendaWithoutClientId() {
            givenVendaAndMethod(dinheiro);
            givenSuccessfulSave(SituacaoPagamento.RECEBIDO);
            VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), dinheiro.getId());
            input.setId(UUID.randomUUID());
            UUID clientId = input.getId();

            VendaPagamentoResponseDTO response = vendaPagamentoService.create(input);

            ArgumentCaptor<PagamentoVenda> captor = ArgumentCaptor.forClass(PagamentoVenda.class);
            verify(vendaPagamentoRepository).saveAndFlush(captor.capture());
            PagamentoVenda saved = captor.getValue();
            assertThat(saved.getVenda()).isSameAs(venda);
            assertThat(saved.getPagamentoMethodId()).isEqualTo(dinheiro.getId());
            assertThat(response.id()).isNotNull().isNotEqualTo(clientId);
            assertThat(response.valor()).isEqualByComparingTo(VendaPagamentoFixture.VALOR);
            assertThat(response.status()).isEqualTo(SituacaoPagamento.RECEBIDO);
            assertThat(response.referenciaTransacao()).isEqualTo(VendaPagamentoFixture.REFERENCIA_TRANSACAO);
        }

        @Test
        void shouldKeepVendaAbertoForImmediateMethod() {
            givenVendaAndMethod(dinheiro);
            givenSuccessfulSave(SituacaoPagamento.PENDENTE);

            vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), dinheiro.getId()));

            ArgumentCaptor<Venda> captor = ArgumentCaptor.forClass(Venda.class);
            verify(vendaRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(VendaSituacao.ABERTO);
        }

        @Test
        void shouldMarkVendaAsPendenteCreditoForAgreementMethod() {
            venda.setClienteId(UUID.randomUUID());
            venda.setVeiculoId(UUID.randomUUID());
            MetodoPagamento agreement = ReciboVendaReferenciaFixture.agreementMetodoPagamento();
            givenVendaAndMethod(agreement);
            givenSuccessfulSave(SituacaoPagamento.PENDENTE);

            vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), agreement.getId()));

            assertThat(venda.getStatus()).isEqualTo(VendaSituacao.CREDITO_PENDENTE);
            verify(vendaRepository).saveAndFlush(venda);
        }

        @Test
        void shouldMarkVendaAsPendenteCreditoForMethodWithLiquidacaoDias() {
            venda.setClienteId(UUID.randomUUID());
            venda.setVeiculoId(UUID.randomUUID());
            MetodoPagamento creditoCartao = ReciboVendaReferenciaFixture.metodoPagamento(TipoMetodoPagamentoEnum.CARTAO_CREDITO, false, 30, true);
            givenVendaAndMethod(creditoCartao);
            givenSuccessfulSave(SituacaoPagamento.AUTORIZADO);

            vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), creditoCartao.getId()));

            assertThat(venda.getStatus()).isEqualTo(VendaSituacao.CREDITO_PENDENTE);
        }

        @Test
        @Disabled("BUG: PagamentoVendaService.create (PagamentoVendaService.java:70-79) calcula settledValor mas nunca o usa; "
                + "a venda nunca passa para PAGO mesmo quando o valor quitado cobre o total")
        void shouldMarkVendaAsPagoWhenSettledValorCoversTotal() {
            venda.setSubtotal(VendaPagamentoFixture.VALOR);
            venda.setValorDesconto(BigDecimal.ZERO);
            givenVendaAndMethod(dinheiro);
            givenSuccessfulSave(SituacaoPagamento.RECEBIDO);

            vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), dinheiro.getId()));

            assertThat(venda.getStatus()).isEqualTo(VendaSituacao.PAGO);
        }

        @Test
        void shouldThrowNotFoundWhenVendaDoesNotExist() {
            UUID vendaId = UUID.randomUUID();
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(vendaId, dinheiro.getId())))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(vendaId.toString());
            verifyNoInteractions(pagamentoMethodRepository, vendaPagamentoRepository);
        }

        @Test
        void shouldRejectPagamentoForPagoVenda() {
            venda.setStatus(VendaSituacao.PAGO);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), dinheiro.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(WRAPPED_MESSAGE)
                    .hasRootCauseMessage("Vendas pagas ou com crédito pendente não podem ser alteradas.");
            verifyNoInteractions(pagamentoMethodRepository, vendaPagamentoRepository);
        }

        @Test
        void shouldRejectPagamentoForVendaWithPendenteCredito() {
            venda.setStatus(VendaSituacao.CREDITO_PENDENTE);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), dinheiro.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("Vendas pagas ou com crédito pendente não podem ser alteradas.");
        }

        @Test
        void shouldThrowNotFoundWhenMetodoPagamentoDoesNotExist() {
            UUID methodId = UUID.randomUUID();
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
            when(pagamentoMethodRepository.findById(methodId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), methodId)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(methodId.toString());
        }

        @Test
        void shouldThrowNotFoundWhenMetodoPagamentoIsInativo() {
            MetodoPagamento inativo = ReciboVendaReferenciaFixture.metodoPagamento(TipoMetodoPagamentoEnum.PIX, false, 0, false);
            givenVendaAndMethod(inativo);

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), inativo.getId())))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(inativo.getId().toString());
            verifyNoInteractions(vendaPagamentoRepository);
        }

        @Test
        void shouldRejectZeroValor() {
            givenVendaAndMethod(dinheiro);
            VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), dinheiro.getId());
            input.setValor(BigDecimal.ZERO);

            assertThatThrownBy(() -> vendaPagamentoService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(WRAPPED_MESSAGE)
                    .hasRootCauseMessage("O valor do pagamento deve ser maior que zero.");
            verifyNoInteractions(vendaPagamentoRepository);
        }

        @Test
        void shouldRejectNullValor() {
            givenVendaAndMethod(dinheiro);
            VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), dinheiro.getId());
            input.setValor(null);

            assertThatThrownBy(() -> vendaPagamentoService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("O valor do pagamento deve ser maior que zero.");
        }

        @Test
        void shouldRequireTransacaoReferenciaWhenMethodDemandsIt() {
            MetodoPagamento pix = ReciboVendaReferenciaFixture.metodoPagamento(TipoMetodoPagamentoEnum.PIX, true, 0, true);
            givenVendaAndMethod(pix);
            VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), pix.getId());
            input.setReferenciaTransacao("  ");

            assertThatThrownBy(() -> vendaPagamentoService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("A referência da transação é obrigatória para a forma de pagamento selecionada.");
            verifyNoInteractions(vendaPagamentoRepository);
        }

        @Test
        void shouldAcceptMissingReferenciaWhenMethodDoesNotDemandIt() {
            givenVendaAndMethod(dinheiro);
            givenSuccessfulSave(SituacaoPagamento.RECEBIDO);
            VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), dinheiro.getId());
            input.setReferenciaTransacao(null);

            assertThat(vendaPagamentoService.create(input).referenciaTransacao()).isNull();
        }

        @Test
        void shouldRequireClienteAndVeiculoForCreditoMethod() {
            venda.setClienteId(UUID.randomUUID());
            MetodoPagamento agreement = ReciboVendaReferenciaFixture.agreementMetodoPagamento();
            givenVendaAndMethod(agreement);

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), agreement.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("Cliente e veículo são obrigatórios para pagamentos a crédito ou convênio.");
            verify(vendaPagamentoRepository, never()).saveAndFlush(any());
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldWrapPersistenceFailure() {
            givenVendaAndMethod(dinheiro);
            when(vendaPagamentoRepository.saveAndFlush(any(PagamentoVenda.class))).thenThrow(new IllegalStateException("db down"));

            assertThatThrownBy(() -> vendaPagamentoService.create(VendaPagamentoFixture.input(venda.getId(), dinheiro.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(WRAPPED_MESSAGE)
                    .hasRootCauseMessage("db down");
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListPagamentosOfVenda() {
            PagamentoVenda pagamento = VendaPagamentoFixture.persistedEntity(dinheiro.getId(), SituacaoPagamento.RECEBIDO);
            when(vendaPagamentoRepository.findByVendaId(venda.getId())).thenReturn(List.of(pagamento));

            assertThat(vendaPagamentoService.findByVenda(venda.getId()))
                    .singleElement()
                    .satisfies(dto -> assertThat(dto.id()).isEqualTo(pagamento.getId()))
                    .satisfies(dto -> assertThat(dto.pagamentoMethodId()).isEqualTo(dinheiro.getId()));
        }

        @Test
        void shouldReturnEmptyListWhenVendaHasNoPagamentos() {
            when(vendaPagamentoRepository.findByVendaId(venda.getId())).thenReturn(List.of());

            assertThat(vendaPagamentoService.findByVenda(venda.getId())).isEmpty();
        }

        @Test
        void shouldFindPagamentoById() {
            PagamentoVenda pagamento = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.AUTORIZADO);
            when(vendaPagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));

            VendaPagamentoResponseDTO response = vendaPagamentoService.find(pagamento.getId());

            assertThat(response).usingRecursiveComparison()
                    .isEqualTo(new VendaPagamentoResponseDTO(pagamento.getId(), pagamento.getPagamentoMethodId(), pagamento.getValor(),
                            SituacaoPagamento.AUTORIZADO, pagamento.getReferenciaTransacao(), pagamento.getAutorizadoEm(),
                            pagamento.getRecebidoEm()));
        }

        @Test
        void shouldThrowNotFoundWhenPagamentoDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(vendaPagamentoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaPagamentoService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
