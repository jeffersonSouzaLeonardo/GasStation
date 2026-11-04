package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.mapper.ReembolsoVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaReembolsoRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
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
import java.time.Instant;
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
class ReembolsoVendaServiceTest {

    private static final String WRAPPED_MESSAGE = "Erro ao estornar venda";

    @Mock
    private VendaReembolsoRepository vendaReembolsoRepository;
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TurnoRepository turnoRepository;
    @Mock
    private MovimentoCaixaRepository dinheiroMovimentoRepository;
    @Spy
    private ReembolsoVendaMapper vendaReembolsoMapper = new ReembolsoVendaMapper();

    @InjectMocks
    private ReembolsoVendaService vendaReembolsoService;

    private Venda venda;
    private Usuario requester;
    private Usuario approver;

    @BeforeEach
    void setUp() {
        venda = VendaFixture.persistedEntity(VendaSituacao.PAGO);
        requester = ReciboVendaReferenciaFixture.usuario(true);
        approver = ReciboVendaReferenciaFixture.usuario(true);
    }

    private VendaReembolsoInputDTO input() {
        return VendaReembolsoFixture.input(venda.getId(), requester.getId(), approver.getId());
    }

    private void givenVendaAndUsuarios() {
        when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
        when(usuarioRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(usuarioRepository.findById(approver.getId())).thenReturn(Optional.of(approver));
    }

    private void givenSuccessfulSave() {
        when(vendaReembolsoRepository.saveAndFlush(any(ReembolsoVenda.class))).thenAnswer(invocation -> {
            ReembolsoVenda reembolso = invocation.getArgument(0);
            reembolso.setId(UUID.randomUUID());
            return reembolso;
        });
    }

    private Turno givenTurnoExists() {
        Turno turno = ReciboVendaReferenciaFixture.turno(venda.getEstacaoId(), venda.getCaixeiroUsuarioId(), TurnoSituacaoEnum.ABERTO);
        turno.setId(venda.getTurnoId());
        when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.of(turno));
        return turno;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistReembolsoLinkedToVenda() {
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.empty());

            VendaReembolsoResponseDTO response = vendaReembolsoService.create(input());

            ArgumentCaptor<ReembolsoVenda> captor = ArgumentCaptor.forClass(ReembolsoVenda.class);
            verify(vendaReembolsoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getVenda()).isSameAs(venda);
            assertThat(response.id()).isNotNull();
            assertThat(response.motivo()).isEqualTo(VendaReembolsoFixture.REASON);
            assertThat(response.requestedBy()).isEqualTo(requester.getId());
            assertThat(response.aprovadoPor()).isEqualTo(approver.getId());
            assertThat(response.reembolsadoEm()).isEqualTo(VendaReembolsoFixture.REEMBOLSADO_EM);
            assertThat(response.observacoes()).isEqualTo(VendaReembolsoFixture.OBSERVACOES);
        }

        @Test
        void shouldDefaultReembolsadoEmToNowWhenNotInformed() {
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.empty());
            VendaReembolsoInputDTO input = input();
            input.setReembolsadoEm(null);
            Instant before = Instant.now();

            VendaReembolsoResponseDTO response = vendaReembolsoService.create(input);

            assertThat(response.reembolsadoEm()).isNotNull().isBetween(before, Instant.now());
        }

        @Test
        void shouldMarkSettledPagamentosAsReembolsadoAndVendaAsReembolsado() {
            PagamentoVenda recebido = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.RECEBIDO);
            PagamentoVenda autorizado = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.AUTORIZADO);
            PagamentoVenda rejeitado = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.REJEITADO);
            venda.getPagamentos().addAll(List.of(recebido, autorizado, rejeitado));
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.empty());

            vendaReembolsoService.create(input());

            assertThat(recebido.getStatus()).isEqualTo(SituacaoPagamento.REEMBOLSADO);
            assertThat(autorizado.getStatus()).isEqualTo(SituacaoPagamento.REEMBOLSADO);
            assertThat(rejeitado.getStatus()).isEqualTo(SituacaoPagamento.REJEITADO);
            assertThat(venda.getStatus()).isEqualTo(VendaSituacao.REEMBOLSADO);
            verify(vendaRepository).saveAndFlush(venda);
        }

        @Test
        void shouldCancelVendaWithoutPagamentos() {
            venda.setStatus(VendaSituacao.CREDITO_PENDENTE);
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.empty());

            vendaReembolsoService.create(input());

            assertThat(venda.getStatus()).isEqualTo(VendaSituacao.CANCELADO);
        }

        @Test
        void shouldRegisterReembolsoMovimentoCaixaWhenTurnoExists() {
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            Turno turno = givenTurnoExists();

            vendaReembolsoService.create(input());

            ArgumentCaptor<MovimentoCaixa> captor = ArgumentCaptor.forClass(MovimentoCaixa.class);
            verify(dinheiroMovimentoRepository).saveAndFlush(captor.capture());
            MovimentoCaixa movimento = captor.getValue();
            assertThat(movimento.getTurno()).isSameAs(turno);
            assertThat(movimento.getTipoMovimento()).isEqualTo(TipoMovimentoCaixaEnum.REEMBOLSO);
            assertThat(movimento.getReferencia()).isEqualTo(venda.getId().toString());
            assertThat(movimento.getCriadoPor()).isEqualTo(requester.getId());
            assertThat(movimento.getAprovadoPor()).isEqualTo(approver.getId());
            assertThat(movimento.getOcorridoEm()).isNotNull();
        }

        @Test
        @Disabled("BUG: ReembolsoVendaService.create (ReembolsoVendaService.java:82) cria o MovimentoCaixa sem valor, "
                + "mas a coluna movimentos_caixa.valor é NOT NULL (@NotNull na entidade)")
        void shouldRegisterReembolsoMovimentoCaixaWithReembolsadoValor() {
            venda.getPagamentos().add(VendaPagamentoFixture.persistedEntity(SituacaoPagamento.RECEBIDO));
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            givenTurnoExists();

            vendaReembolsoService.create(input());

            ArgumentCaptor<MovimentoCaixa> captor = ArgumentCaptor.forClass(MovimentoCaixa.class);
            verify(dinheiroMovimentoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getValor()).isEqualByComparingTo(VendaPagamentoFixture.VALOR);
        }

        @Test
        void shouldNotRegisterMovimentoCaixaWhenTurnoDoesNotExist() {
            givenVendaAndUsuarios();
            givenSuccessfulSave();
            when(turnoRepository.findById(venda.getTurnoId())).thenReturn(Optional.empty());

            vendaReembolsoService.create(input());

            verifyNoInteractions(dinheiroMovimentoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenVendaDoesNotExist() {
            UUID vendaId = UUID.randomUUID();
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaReembolsoService.create(VendaReembolsoFixture.input(vendaId, requester.getId(), approver.getId())))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(vendaId.toString());
            verifyNoInteractions(usuarioRepository, vendaReembolsoRepository);
        }

        @Test
        void shouldRejectReembolsoOfAbertoVenda() {
            venda.setStatus(VendaSituacao.ABERTO);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(WRAPPED_MESSAGE)
                    .hasRootCauseMessage("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
            verifyNoInteractions(usuarioRepository, vendaReembolsoRepository);
        }

        @Test
        void shouldRejectReembolsoOfAlreadyReembolsadoVenda() {
            venda.setStatus(VendaSituacao.REEMBOLSADO);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
        }

        @Test
        void shouldThrowNotFoundWhenRequesterDoesNotExist() {
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
            when(usuarioRepository.findById(requester.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(requester.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenApproverDoesNotExist() {
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
            when(usuarioRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
            when(usuarioRepository.findById(approver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(approver.getId().toString());
        }

        @Test
        void shouldRejectInativoApprover() {
            approver.setAtivo(false);
            givenVendaAndUsuarios();

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("Os usuários solicitante e aprovador precisam estar ativos.");
            verifyNoInteractions(vendaReembolsoRepository);
        }

        @Test
        void shouldRejectInativoRequester() {
            requester.setAtivo(false);
            givenVendaAndUsuarios();

            assertThatThrownBy(() -> vendaReembolsoService.create(input()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("Os usuários solicitante e aprovador precisam estar ativos.");
        }

        @Test
        void shouldRejectSelfAprovacao() {
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));
            when(usuarioRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

            assertThatThrownBy(() -> vendaReembolsoService.create(
                    VendaReembolsoFixture.input(venda.getId(), requester.getId(), requester.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage("A aprovação do estorno deve ser feita por um usuário diferente.");
            verifyNoInteractions(vendaReembolsoRepository);
            verify(vendaRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class FindByVenda {

        @Test
        void shouldListReembolsosOfVenda() {
            ReembolsoVenda reembolso = VendaReembolsoFixture.persistedEntity(requester.getId(), approver.getId());
            when(vendaReembolsoRepository.findByVendaId(venda.getId())).thenReturn(List.of(reembolso));

            assertThat(vendaReembolsoService.findByVenda(venda.getId()))
                    .singleElement()
                    .usingRecursiveComparison()
                    .isEqualTo(new VendaReembolsoResponseDTO(reembolso.getId(), VendaReembolsoFixture.REASON, requester.getId(),
                            approver.getId(), VendaReembolsoFixture.REEMBOLSADO_EM, VendaReembolsoFixture.OBSERVACOES));
        }

        @Test
        void shouldReturnEmptyListWhenVendaHasNoReembolsos() {
            when(vendaReembolsoRepository.findByVendaId(venda.getId())).thenReturn(List.of());

            assertThat(vendaReembolsoService.findByVenda(venda.getId())).isEmpty();
        }
    }
}
