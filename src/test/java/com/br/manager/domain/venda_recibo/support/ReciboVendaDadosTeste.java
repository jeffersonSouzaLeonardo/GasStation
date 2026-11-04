package com.br.manager.domain.venda_recibo.support;

import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.repository.ClienteRepository;
import com.br.manager.domain.operacional.repository.VeiculoClienteRepository;
import com.br.manager.domain.operacional.repository.BicoRepository;
import com.br.manager.domain.operacional.repository.MetodoPagamentoRepository;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
import com.br.manager.domain.operacional.repository.BombaRepository;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EmpresaRepository;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import org.springframework.context.ApplicationContext;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cria, via repositorios, os pre-requisitos de outros dominios (empresa, posto, usuarios, turno,
 * cliente, produto, bico, forma de pagamento) e vendas ja persistidas para os testes de integracao de venda.
 */
public class ReciboVendaDadosTeste {

    private static final AtomicLong SEQUENCE = new AtomicLong(10_000_000_000_000L);

    private final EmpresaRepository empresaRepository;
    private final EstacaoRepository estacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoClienteRepository clienteVeiculoRepository;
    private final ProdutoRepository produtoRepository;
    private final TanqueRepository tanqueRepository;
    private final BombaRepository bombaRepository;
    private final BicoRepository bicoRepository;
    private final MetodoPagamentoRepository pagamentoMethodRepository;
    private final VendaRepository vendaRepository;

    public ReciboVendaDadosTeste(ApplicationContext context) {
        this.empresaRepository = context.getBean(EmpresaRepository.class);
        this.estacaoRepository = context.getBean(EstacaoRepository.class);
        this.usuarioRepository = context.getBean(UsuarioRepository.class);
        this.turnoRepository = context.getBean(TurnoRepository.class);
        this.clienteRepository = context.getBean(ClienteRepository.class);
        this.clienteVeiculoRepository = context.getBean(VeiculoClienteRepository.class);
        this.produtoRepository = context.getBean(ProdutoRepository.class);
        this.tanqueRepository = context.getBean(TanqueRepository.class);
        this.bombaRepository = context.getBean(BombaRepository.class);
        this.bicoRepository = context.getBean(BicoRepository.class);
        this.pagamentoMethodRepository = context.getBean(MetodoPagamentoRepository.class);
        this.vendaRepository = context.getBean(VendaRepository.class);
    }

    public record VendaScenario(UUID estacaoId, UUID turnoId, UUID caixeiroUsuarioId, UUID produtoId) {
    }

    public VendaScenario createScenario() {
        UUID estacaoId = createEstacao(true);
        UUID caixeiroUsuarioId = createUsuario(true);
        UUID turnoId = createTurno(estacaoId, caixeiroUsuarioId, TurnoSituacaoEnum.ABERTO);
        return new VendaScenario(estacaoId, turnoId, caixeiroUsuarioId, createProduto());
    }

    public UUID createEstacao(boolean ativo) {
        Empresa empresa = ReciboVendaReferenciaFixture.empresa(String.valueOf(SEQUENCE.incrementAndGet()));
        empresa.setId(null);
        UUID empresaId = empresaRepository.saveAndFlush(empresa).getId();

        Estacao estacao = ReciboVendaReferenciaFixture.estacao(empresaId, ativo);
        estacao.setId(null);
        return estacaoRepository.saveAndFlush(estacao).getId();
    }

    public UUID createUsuario(boolean ativo) {
        Usuario usuario = ReciboVendaReferenciaFixture.usuario(ativo);
        usuario.setId(null);
        return usuarioRepository.saveAndFlush(usuario).getId();
    }

    public UUID createTurno(UUID estacaoId, UUID caixeiroUsuarioId, TurnoSituacaoEnum status) {
        Turno turno = ReciboVendaReferenciaFixture.turno(estacaoId, caixeiroUsuarioId, status);
        return turnoRepository.saveAndFlush(turno).getId();
    }

    public UUID createCliente() {
        Cliente cliente = ReciboVendaReferenciaFixture.cliente(true);
        cliente.setId(null);
        return clienteRepository.saveAndFlush(cliente).getId();
    }

    public UUID createVeiculo(UUID clienteId) {
        VeiculoCliente veiculo = ReciboVendaReferenciaFixture.veiculo(clienteId, true);
        veiculo.setId(null);
        return clienteVeiculoRepository.saveAndFlush(veiculo).getId();
    }

    public UUID createProduto() {
        Produto produto = ReciboVendaReferenciaFixture.produto(true);
        produto.setId(null);
        return produtoRepository.saveAndFlush(produto).getId();
    }

    public UUID createBico(UUID produtoId) {
        Tanque tanque = ReciboVendaReferenciaFixture.tanque(produtoId);
        tanque.setId(null);
        UUID tanqueId = tanqueRepository.saveAndFlush(tanque).getId();

        Bomba bomba = ReciboVendaReferenciaFixture.bomba();
        bomba.setId(null);
        UUID bombaId = bombaRepository.saveAndFlush(bomba).getId();

        Bico bico = ReciboVendaReferenciaFixture.bico(bombaId, tanqueId, produtoId);
        bico.setId(null);
        return bicoRepository.saveAndFlush(bico).getId();
    }

    public UUID createMetodoPagamento(MetodoPagamento metodoPagamento) {
        metodoPagamento.setId(null);
        return pagamentoMethodRepository.saveAndFlush(metodoPagamento).getId();
    }

    public Venda createVenda(VendaScenario scenario, String numeroVenda, VendaSituacao status) {
        Venda venda = VendaFixture.entity(scenario.estacaoId(), scenario.turnoId(), scenario.caixeiroUsuarioId(), status);
        venda.setNumeroVenda(numeroVenda);
        venda.adicionarItem(VendaItemFixture.persistedEntity(scenario.produtoId()));
        return vendaRepository.saveAndFlush(venda);
    }

    public Venda createVendaWithPagamento(VendaScenario scenario, VendaSituacao status, UUID pagamentoMethodId,
                                      SituacaoPagamento pagamentoSituacao) {
        Venda venda = VendaFixture.entity(scenario.estacaoId(), scenario.turnoId(), scenario.caixeiroUsuarioId(), status);
        venda.adicionarItem(VendaItemFixture.persistedEntity(scenario.produtoId()));
        venda.adicionarPagamento(VendaPagamentoFixture.persistedEntity(pagamentoMethodId, pagamentoSituacao));
        return vendaRepository.saveAndFlush(venda);
    }

    public Venda createReembolsadoVenda(VendaScenario scenario, UUID requestedBy, UUID aprovadoPor) {
        Venda venda = VendaFixture.entity(scenario.estacaoId(), scenario.turnoId(), scenario.caixeiroUsuarioId(), VendaSituacao.REEMBOLSADO);
        venda.adicionarItem(VendaItemFixture.persistedEntity(scenario.produtoId()));
        ReembolsoVenda reembolso = VendaReembolsoFixture.persistedEntity(requestedBy, aprovadoPor);
        venda.adicionarReembolso(reembolso);
        return vendaRepository.saveAndFlush(venda);
    }
}
