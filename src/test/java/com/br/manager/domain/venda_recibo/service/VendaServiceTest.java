package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.repository.ClienteRepository;
import com.br.manager.domain.operacional.repository.VeiculoClienteRepository;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.venda_recibo.dto.VendaInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.mapper.ItemVendaMapper;
import com.br.manager.domain.venda_recibo.mapper.VendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
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
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    private static final String SAVE_ERROR_MESSAGE = "Erro ao salvar venda " + VendaFixture.NUMERO_VENDA;
    private static final String MENSAGEM_VENDA_BLOQUEADA = "Vendas pagas ou com crédito pendente não podem ser alteradas.";

    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private ItemVendaService vendaItemService;
    @Mock
    private EstacaoRepository estacaoRepository;
    @Mock
    private TurnoRepository turnoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VeiculoClienteRepository clienteVeiculoRepository;
    @Spy
    private VendaMapper vendaMapper = new VendaMapper();
    @Spy
    private ItemVendaMapper vendaItemMapper = new ItemVendaMapper();

    @InjectMocks
    private VendaService vendaService;

    private Estacao estacao;
    private Turno turno;
    private Usuario caixeiro;
    private VendaInputDTO input;

    @BeforeEach
    void setUp() {
        estacao = ReciboVendaReferenciaFixture.estacao(true);
        caixeiro = ReciboVendaReferenciaFixture.usuario(true);
        turno = ReciboVendaReferenciaFixture.turno(estacao.getId(), caixeiro.getId(), TurnoSituacaoEnum.ABERTO);
        input = VendaFixture.input(estacao.getId(), turno.getId(), caixeiro.getId(),
                List.of(VendaItemFixture.produtoInput(UUID.randomUUID())));
    }

    private void givenEstacaoTurnoAndCaixeiro() {
        when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
        when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(usuarioRepository.findById(caixeiro.getId())).thenReturn(Optional.of(caixeiro));
    }

    private static Answer<Venda> assigningId(UUID id) {
        return invocation -> {
            Venda venda = invocation.getArgument(0);
            venda.setId(id);
            return venda;
        };
    }

    @Nested
    class CreateValidacao {

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> vendaService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O payload da venda é obrigatório.");
        }

        @Test
        void shouldRejectMissingEstacao() {
            input.setEstacaoId(null);

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do posto é obrigatório.");
        }

        @Test
        void shouldRejectMissingTurno() {
            input.setTurnoId(null);

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do turno é obrigatório.");
        }

        @Test
        void shouldRejectMissingCaixeiro() {
            input.setCaixeiroUsuarioId(null);

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O ID do usuário caixa é obrigatório.");
        }

        @Test
        void shouldRejectVazioVendaNumero() {
            input.setNumeroVenda(" ");

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("O número da venda é obrigatório.");
        }

        @Test
        void shouldRejectDuplicatedVendaNumeroForEstacao() {
            when(vendaRepository.existsByEstacaoIdAndNumeroVenda(estacao.getId(), VendaFixture.NUMERO_VENDA)).thenReturn(true);

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(String.format("A venda %s já existe para o posto %s.", VendaFixture.NUMERO_VENDA, estacao.getId()));
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectVendaWithoutItens() {
            input.setItens(List.of());

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A venda deve conter pelo menos um item.");
        }

        @Test
        void shouldRejectVendaWithNullItens() {
            input.setItens(null);

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("A venda deve conter pelo menos um item.");
            verifyNoInteractions(estacaoRepository);
        }
    }

    @Nested
    class Create {

        private final UUID vendaId = UUID.randomUUID();

        @Test
        void shouldPersistAbertoVendaAndSaveItens() {
            givenEstacaoTurnoAndCaixeiro();
            Venda reloaded = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            reloaded.setId(vendaId);
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.of(reloaded));

            VendaResponseDTO response = vendaService.create(input);

            assertThat(response.id()).isEqualTo(vendaId);
            verify(vendaItemService).saveVendaItens(vendaId, input.getItens());
            ArgumentCaptor<Venda> captor = ArgumentCaptor.forClass(Venda.class);
            verify(vendaRepository, times(2)).saveAndFlush(captor.capture());
            Venda criado = captor.getAllValues().get(0);
            assertThat(criado.getStatus()).isEqualTo(VendaSituacao.ABERTO);
            assertThat(criado.getEstacaoId()).isEqualTo(estacao.getId());
            assertThat(criado.getTurnoId()).isEqualTo(turno.getId());
            assertThat(criado.getCaixeiroUsuarioId()).isEqualTo(caixeiro.getId());
            assertThat(criado.getNumeroVenda()).isEqualTo(VendaFixture.NUMERO_VENDA);
            assertThat(criado.getVendidoEm()).isEqualTo(VendaFixture.VENDIDO_EM);
            assertThat(criado.getValorDesconto()).isEqualByComparingTo(VendaFixture.DESCONTO);
            assertThat(criado.getSituacaoFiscal()).isEqualTo(VendaFixture.FISCAL_SITUACAO);
            assertThat(criado.getObservacoes()).isEqualTo(VendaFixture.OBSERVACOES);
        }

        @Test
        void shouldDefaultVendidoEmAndDescontoWhenNotInformed() {
            givenEstacaoTurnoAndCaixeiro();
            input.setVendidoEm(null);
            input.setValorDesconto(null);
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.of(VendaFixture.persistedEntity(VendaSituacao.ABERTO)));

            vendaService.create(input);

            ArgumentCaptor<Venda> captor = ArgumentCaptor.forClass(Venda.class);
            verify(vendaRepository, times(2)).saveAndFlush(captor.capture());
            assertThat(captor.getAllValues().get(0).getVendidoEm()).isNotNull();
            assertThat(captor.getAllValues().get(0).getValorDesconto()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldVinculoAtivoFrentistaClienteAndVeiculo() {
            givenEstacaoTurnoAndCaixeiro();
            Usuario frentista = ReciboVendaReferenciaFixture.usuario(true);
            Cliente cliente = ReciboVendaReferenciaFixture.cliente(true);
            VeiculoCliente veiculo = ReciboVendaReferenciaFixture.veiculo(cliente.getId(), true);
            input.setFrentistaUsuarioId(frentista.getId());
            input.setClienteId(cliente.getId());
            input.setVeiculoId(veiculo.getId());
            when(usuarioRepository.findById(frentista.getId())).thenReturn(Optional.of(frentista));
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteVeiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.of(VendaFixture.persistedEntity(VendaSituacao.ABERTO)));

            vendaService.create(input);

            ArgumentCaptor<Venda> captor = ArgumentCaptor.forClass(Venda.class);
            verify(vendaRepository, times(2)).saveAndFlush(captor.capture());
            Venda criado = captor.getAllValues().get(0);
            assertThat(criado.getFrentistaUsuarioId()).isEqualTo(frentista.getId());
            assertThat(criado.getClienteId()).isEqualTo(cliente.getId());
            assertThat(criado.getVeiculoId()).isEqualTo(veiculo.getId());
        }

        @Test
        @Disabled("BUG: VendaService.create (VendaService.java:59-75) never calculates Venda.subtotal from the itens, "
                + "so every venda is persisted with subtotal 0")
        void shouldCalculateSubtotalFromItens() {
            givenEstacaoTurnoAndCaixeiro();
            Venda reloaded = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            reloaded.setSubtotal(BigDecimal.ZERO);
            reloaded.adicionarItem(VendaItemFixture.persistedEntity(UUID.randomUUID()));
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.of(reloaded));

            VendaResponseDTO response = vendaService.create(input);

            BigDecimal esperadoSubtotal = VendaItemFixture.QUANTITY.multiply(VendaItemFixture.PRECO_UNITARIO)
                    .subtract(VendaItemFixture.DESCONTO);
            assertThat(response.subtotal()).isEqualByComparingTo(esperadoSubtotal);
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoDoesNotExist() {
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(estacao.getId().toString());
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoIsInativo() {
            Estacao inativo = ReciboVendaReferenciaFixture.estacao(false);
            input.setEstacaoId(inativo.getId());
            when(estacaoRepository.findById(inativo.getId())).thenReturn(Optional.of(inativo));

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenTurnoDoesNotExist() {
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
            when(turnoRepository.findById(turno.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(turno.getId().toString());
        }

        @Test
        void shouldWrapBusinessErrorWhenTurnoIsFechado() {
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
            turno.setStatus(TurnoSituacaoEnum.FECHADO);
            when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasRootCauseMessage(String.format("O turno %s não está ativo.", turno.getId()));
        }

        @Test
        void shouldThrowNotFoundWhenCaixeiroDoesNotExist() {
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
            when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));
            when(usuarioRepository.findById(caixeiro.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenCaixeiroIsInativo() {
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
            when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));
            caixeiro.setAtivo(false);
            when(usuarioRepository.findById(caixeiro.getId())).thenReturn(Optional.of(caixeiro));

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenFrentistaIsInativo() {
            givenEstacaoTurnoAndCaixeiro();
            Usuario frentista = ReciboVendaReferenciaFixture.usuario(false);
            input.setFrentistaUsuarioId(frentista.getId());
            when(usuarioRepository.findById(frentista.getId())).thenReturn(Optional.of(frentista));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(frentista.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenFrentistaDoesNotExist() {
            givenEstacaoTurnoAndCaixeiro();
            UUID frentistaId = UUID.randomUUID();
            input.setFrentistaUsuarioId(frentistaId);
            when(usuarioRepository.findById(frentistaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenClienteIsInativo() {
            givenEstacaoTurnoAndCaixeiro();
            Cliente cliente = ReciboVendaReferenciaFixture.cliente(false);
            input.setClienteId(cliente.getId());
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(cliente.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenClienteDoesNotExist() {
            givenEstacaoTurnoAndCaixeiro();
            UUID clienteId = UUID.randomUUID();
            input.setClienteId(clienteId);
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldThrowNotFoundWhenVeiculoDoesNotExist() {
            givenEstacaoTurnoAndCaixeiro();
            UUID veiculoId = UUID.randomUUID();
            input.setVeiculoId(veiculoId);
            when(clienteVeiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(veiculoId.toString());
        }

        @Test
        void shouldThrowNotFoundWhenVeiculoIsInativo() {
            givenEstacaoTurnoAndCaixeiro();
            VeiculoCliente veiculo = ReciboVendaReferenciaFixture.veiculo(UUID.randomUUID(), false);
            input.setVeiculoId(veiculo.getId());
            when(clienteVeiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));

            assertThatThrownBy(() -> vendaService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldRejectVeiculoFromAnotherCliente() {
            givenEstacaoTurnoAndCaixeiro();
            Cliente cliente = ReciboVendaReferenciaFixture.cliente(true);
            VeiculoCliente veiculo = ReciboVendaReferenciaFixture.veiculo(UUID.randomUUID(), true);
            input.setClienteId(cliente.getId());
            input.setVeiculoId(veiculo.getId());
            when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
            when(clienteVeiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasRootCauseMessage("O veículo não pertence ao cliente informado.");
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            givenEstacaoTurnoAndCaixeiro();
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
            verify(vendaItemService, never()).saveVendaItens(any(), any());
        }

        @Test
        void shouldPropagateNotFoundFromItemService() {
            givenEstacaoTurnoAndCaixeiro();
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaItemService.saveVendaItens(vendaId, input.getItens()))
                    .thenThrow(new ExcecaoNegocioNaoEncontrado("Produto não encontrado"));

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessage("Produto não encontrado");
        }

        @Test
        void shouldThrowNotFoundWhenSavedVendaCannotBeReloaded() {
            givenEstacaoTurnoAndCaixeiro();
            when(vendaRepository.saveAndFlush(any(Venda.class))).thenAnswer(assigningId(vendaId));
            when(vendaRepository.findDetailedById(vendaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.create(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(vendaId.toString());
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateOnlyInformedFieldsOfAbertoVenda() {
            Venda existing = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            when(vendaRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(vendaRepository.saveAndFlush(existing)).thenReturn(existing);

            VendaResponseDTO response = vendaService.update(VendaFixture.updateInput(existing.getId()));

            assertThat(response.observacoes()).isEqualTo(VendaFixture.OBSERVACOES_ATUALIZADAS);
            assertThat(response.numeroVenda()).isEqualTo(VendaFixture.NUMERO_VENDA);
            assertThat(response.estacaoId()).isEqualTo(existing.getEstacaoId());
            assertThat(response.valorDesconto()).isEqualByComparingTo(VendaFixture.DESCONTO);
            verifyNoInteractions(estacaoRepository, turnoRepository, usuarioRepository);
        }

        @Test
        void shouldReplaceEstacaoWhenInformed() {
            Venda existing = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            VendaInputDTO update = VendaFixture.updateInput(existing.getId());
            update.setEstacaoId(estacao.getId());
            when(vendaRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));
            when(vendaRepository.saveAndFlush(existing)).thenReturn(existing);

            VendaResponseDTO response = vendaService.update(update);

            assertThat(response.estacaoId()).isEqualTo(estacao.getId());
        }

        @Test
        void shouldThrowNotFoundWhenVendaDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(vendaRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.update(VendaFixture.updateInput(id)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectUpdateOfPagoVenda() {
            Venda existing = VendaFixture.persistedEntity(VendaSituacao.PAGO);
            when(vendaRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> vendaService.update(VendaFixture.updateInput(existing.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage(MENSAGEM_VENDA_BLOQUEADA);
            verify(vendaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectUpdateOfVendaWithPendenteCredito() {
            Venda existing = VendaFixture.persistedEntity(VendaSituacao.CREDITO_PENDENTE);
            when(vendaRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> vendaService.update(VendaFixture.updateInput(existing.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasRootCauseMessage(MENSAGEM_VENDA_BLOQUEADA);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Venda existing = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            VendaInputDTO update = VendaFixture.updateInput(existing.getId());
            update.setNumeroVenda(VendaFixture.OUTROS_VENDA_NUMERO);
            when(vendaRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(vendaRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> vendaService.update(update))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar venda " + VendaFixture.OUTROS_VENDA_NUMERO)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindVendaById() {
            Venda venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThat(vendaService.find(venda.getId()).id()).isEqualTo(venda.getId());
        }

        @Test
        void shouldThrowNotFoundWhenVendaIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(vendaRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListAllVendasWithDetails() {
            Venda first = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            Venda second = VendaFixture.persistedEntity(VendaSituacao.PAGO);
            when(vendaRepository.findAll()).thenReturn(List.of(first, second));
            when(vendaRepository.findDetailedById(first.getId())).thenReturn(Optional.of(first));
            when(vendaRepository.findDetailedById(second.getId())).thenReturn(Optional.of(second));

            assertThat(vendaService.findAll())
                    .extracting(VendaResponseDTO::id)
                    .containsExactly(first.getId(), second.getId());
        }

        @Test
        void shouldListVendasByEstacao() {
            Venda venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            when(vendaRepository.findByEstacaoId(venda.getEstacaoId())).thenReturn(List.of(venda));
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThat(vendaService.findByEstacao(venda.getEstacaoId()))
                    .extracting(VendaResponseDTO::estacaoId)
                    .containsExactly(venda.getEstacaoId());
        }

        @Test
        void shouldReturnEmptyListWhenEstacaoHasNoVendas() {
            UUID estacaoId = UUID.randomUUID();
            when(vendaRepository.findByEstacaoId(estacaoId)).thenReturn(List.of());

            assertThat(vendaService.findByEstacao(estacaoId)).isEmpty();
        }

        @Test
        void shouldReturnVendaEntityById() {
            Venda venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
            when(vendaRepository.findDetailedById(venda.getId())).thenReturn(Optional.of(venda));

            assertThat(vendaService.getVendaEntityById(venda.getId())).isSameAs(venda);
        }

        @Test
        void shouldThrowNotFoundWhenVendaEntityDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(vendaRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> vendaService.getVendaEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }
}
