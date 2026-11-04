package com.br.manager.domain.turno_caixa.support;

import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
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
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pre-requisitos de outros dominios (empresa, posto, usuario, bico, forma de pagamento, turno)
 * usados pelos testes de sessao de caixa, movimentacoes, leituras de bico e fechamentos de pagamento.
 * Os metodos estaticos montam entidades em memoria (testes unitarios); os de instancia persistem (ITs).
 */
@TestComponent
public class TurnoBTestData {

    public static final BigDecimal TURNO_ABERTURA_DINHEIRO = new BigDecimal("200.00");
    public static final LocalDateTime TURNO_OPENED_AT = LocalDateTime.of(2026, 10, 31, 6, 0);
    public static final BigDecimal MEDIDOR_BICO = new BigDecimal("1000.0000");

    /**
     * Le valores numericos do JSON como BigDecimal, permitindo comparar valores monetarios sem perda de precisao.
     */
    public static final RestAssuredConfig DECIMAL_JSON = RestAssuredConfig.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final EmpresaRepository empresaRepository;
    private final EstacaoRepository estacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final TanqueRepository tanqueRepository;
    private final BombaRepository bombaRepository;
    private final BicoRepository bicoRepository;
    private final MetodoPagamentoRepository pagamentoMethodRepository;
    private final TurnoRepository turnoRepository;

    public TurnoBTestData(EmpresaRepository empresaRepository,
                          EstacaoRepository estacaoRepository,
                          UsuarioRepository usuarioRepository,
                          ProdutoRepository produtoRepository,
                          TanqueRepository tanqueRepository,
                          BombaRepository bombaRepository,
                          BicoRepository bicoRepository,
                          MetodoPagamentoRepository pagamentoMethodRepository,
                          TurnoRepository turnoRepository) {
        this.empresaRepository = empresaRepository;
        this.estacaoRepository = estacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.tanqueRepository = tanqueRepository;
        this.bombaRepository = bombaRepository;
        this.bicoRepository = bicoRepository;
        this.pagamentoMethodRepository = pagamentoMethodRepository;
        this.turnoRepository = turnoRepository;
    }

    public static Turno turnoEntity(TurnoSituacaoEnum status) {
        Turno turno = new Turno();
        turno.setId(UUID.randomUUID());
        turno.setEstacaoId(UUID.randomUUID());
        turno.setCaixeiroUsuarioId(UUID.randomUUID());
        turno.setOpenedBy(turno.getCaixeiroUsuarioId());
        turno.setFundoAbertura(TURNO_ABERTURA_DINHEIRO);
        turno.setOpenedAt(TURNO_OPENED_AT);
        turno.setStatus(status);
        return turno;
    }

    public UUID createUsuario() {
        return saveUsuario(true);
    }

    public UUID createInativoUsuario() {
        return saveUsuario(false);
    }

    public UUID createEstacao() {
        long sequence = SEQUENCE.incrementAndGet();
        Empresa empresa = new Empresa();
        empresa.setLegalName("Empresa TurnoB " + sequence);
        empresa.setCnpj(String.format("%014d", sequence));
        empresa.setAtivo(true);
        UUID empresaId = empresaRepository.saveAndFlush(empresa).getId();

        Estacao estacao = new Estacao();
        estacao.setEmpresaId(empresaId);
        estacao.setNome("Posto TurnoB " + sequence);
        estacao.setAtivo(true);
        return estacaoRepository.saveAndFlush(estacao).getId();
    }

    public UUID createBico() {
        long sequence = SEQUENCE.incrementAndGet();
        Produto produto = new Produto();
        produto.setSku("GAS-" + sequence);
        produto.setNome("Gasolina Comum");
        produto.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        produto.setUnidade(TipoUnidadeEnum.L);
        produto.setAtivo(true);
        UUID produtoId = produtoRepository.saveAndFlush(produto).getId();

        Tanque tanque = new Tanque();
        tanque.setCodigo("TQ-" + sequence);
        tanque.setProdutoId(produtoId);
        tanque.setCapacidadeLitros(new BigDecimal("15000.0000"));
        tanque.setAtivo(true);
        UUID tanqueId = tanqueRepository.saveAndFlush(tanque).getId();

        Bomba bomba = new Bomba();
        bomba.setCodigo("BB-" + sequence);
        bomba.setAtivo(true);
        UUID bombaId = bombaRepository.saveAndFlush(bomba).getId();

        Bico bico = new Bico();
        bico.setBombaId(bombaId);
        bico.setTanqueId(tanqueId);
        bico.setProdutoId(produtoId);
        bico.setCodigo("BC-" + sequence);
        bico.setNumeroMedidor(MEDIDOR_BICO);
        bico.setAtivo(true);
        return bicoRepository.saveAndFlush(bico).getId();
    }

    public UUID createMetodoPagamento() {
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setNome("Dinheiro " + SEQUENCE.incrementAndGet());
        metodoPagamento.setTipo(TipoMetodoPagamentoEnum.DINHEIRO);
        metodoPagamento.setExigeReferencia(false);
        metodoPagamento.setAtivo(true);
        return pagamentoMethodRepository.saveAndFlush(metodoPagamento).getId();
    }

    public Turno createAbertoTurno() {
        return saveTurno(TurnoSituacaoEnum.ABERTO);
    }

    public Turno createFechadoTurno() {
        return saveTurno(TurnoSituacaoEnum.FECHADO);
    }

    private Turno saveTurno(TurnoSituacaoEnum status) {
        Turno turno = turnoEntity(status);
        turno.setEstacaoId(createEstacao());
        turno.setCaixeiroUsuarioId(createUsuario());
        turno.setOpenedBy(turno.getCaixeiroUsuarioId());
        return turnoRepository.saveAndFlush(turno);
    }

    private UUID saveUsuario(boolean ativo) {
        long sequence = SEQUENCE.incrementAndGet();
        Usuario usuario = new Usuario();
        usuario.setNome("Operador TurnoB " + sequence);
        usuario.setEmail("operador.shiftb." + sequence + "@posto.com.br");
        usuario.setPasswordHash("hash-" + sequence);
        usuario.setAtivo(ativo);
        return usuarioRepository.saveAndFlush(usuario).getId();
    }
}
