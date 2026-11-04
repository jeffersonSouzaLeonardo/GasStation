package com.br.manager.domain.venda_recibo.fixture;

import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.enums.TipoPessoaEnum;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidades de outros dominios usadas como pre-requisito pelos cenarios de venda.
 * Todas sao criadas com ID aleatorio (para testes unitarios); quem persiste deve limpar o ID quando gerado pelo banco.
 */
public final class ReciboVendaReferenciaFixture {

    public static final LocalDateTime CRIADO_EM = LocalDateTime.of(2026, 10, 1, 8, 0);
    public static final String FusoHorario = "America/Sao_Paulo";
    public static final String PASSWORD_HASH = "$2a$10$hash";
    public static final BigDecimal FUNDO_ABERTURA = new BigDecimal("100.00");
    public static final BigDecimal TANQUE_CAPACIDADE = new BigDecimal("15000.0000");

    private ReciboVendaReferenciaFixture() {
    }

    public static Empresa empresa(String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setLegalName("Rede Posto Venda LTDA");
        empresa.setTradeName("Posto Venda");
        empresa.setCnpj(cnpj);
        empresa.setAtivo(true);
        return empresa;
    }

    public static Estacao estacao(UUID empresaId, boolean ativo) {
        Estacao estacao = new Estacao();
        estacao.setId(UUID.randomUUID());
        estacao.setEmpresaId(empresaId);
        estacao.setNome("Posto Centro");
        estacao.setFusoHorario(FusoHorario);
        estacao.setAtivo(ativo);
        return estacao;
    }

    public static Estacao estacao(boolean ativo) {
        return estacao(UUID.randomUUID(), ativo);
    }

    public static Usuario usuario(String email, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Operador " + email);
        usuario.setEmail(email);
        usuario.setPasswordHash(PASSWORD_HASH);
        usuario.setAtivo(ativo);
        usuario.setCriadoEm(CRIADO_EM);
        return usuario;
    }

    public static Usuario usuario(boolean ativo) {
        return usuario(UUID.randomUUID() + "@posto.com.br", ativo);
    }

    public static Turno turno(UUID estacaoId, UUID caixeiroUsuarioId, TurnoSituacaoEnum status) {
        Turno turno = new Turno();
        turno.setId(UUID.randomUUID());
        turno.setEstacaoId(estacaoId);
        turno.setCaixeiroUsuarioId(caixeiroUsuarioId);
        turno.setOpenedBy(caixeiroUsuarioId);
        turno.setOpenedAt(CRIADO_EM);
        turno.setFundoAbertura(FUNDO_ABERTURA);
        turno.setStatus(status);
        return turno;
    }

    public static Turno turno(TurnoSituacaoEnum status) {
        return turno(UUID.randomUUID(), UUID.randomUUID(), status);
    }

    public static Cliente cliente(boolean ativo) {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("Transportadora Cliente");
        cliente.setPersonType(TipoPessoaEnum.JURIDICA);
        cliente.setAtivo(ativo);
        return cliente;
    }

    public static VeiculoCliente veiculo(UUID clienteId, boolean ativo) {
        VeiculoCliente veiculo = new VeiculoCliente();
        veiculo.setId(UUID.randomUUID());
        veiculo.setClienteId(clienteId);
        veiculo.setPlaca("ABC1D23");
        veiculo.setAtivo(ativo);
        return veiculo;
    }

    public static Produto produto(String sku, boolean ativo) {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setSku(sku);
        produto.setNome("Gasolina Comum");
        produto.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        produto.setUnidade(TipoUnidadeEnum.L);
        produto.setAtivo(ativo);
        produto.setCriadoEm(CRIADO_EM);
        return produto;
    }

    public static Produto produto(boolean ativo) {
        return produto("SKU-" + UUID.randomUUID(), ativo);
    }

    public static Tanque tanque(UUID produtoId) {
        Tanque tanque = new Tanque();
        tanque.setId(UUID.randomUUID());
        tanque.setCodigo("TQ-01");
        tanque.setProdutoId(produtoId);
        tanque.setCapacidadeLitros(TANQUE_CAPACIDADE);
        tanque.setSaldoContabilLitros(BigDecimal.ZERO);
        tanque.setAtivo(true);
        tanque.setCriadoEm(CRIADO_EM);
        return tanque;
    }

    public static Bomba bomba() {
        Bomba bomba = new Bomba();
        bomba.setId(UUID.randomUUID());
        bomba.setCodigo("BB-01");
        bomba.setAtivo(true);
        bomba.setCriadoEm(CRIADO_EM);
        return bomba;
    }

    public static Bico bico(UUID bombaId, UUID tanqueId, UUID produtoId) {
        Bico bico = new Bico();
        bico.setId(UUID.randomUUID());
        bico.setBombaId(bombaId);
        bico.setTanqueId(tanqueId);
        bico.setProdutoId(produtoId);
        bico.setCodigo("BC-01");
        bico.setAtivo(true);
        bico.setCriadoEm(CRIADO_EM);
        return bico;
    }

    public static MetodoPagamento metodoPagamento(TipoMetodoPagamentoEnum tipo, boolean exigeReferencia,
                                              int diasLiquidacao, boolean ativo) {
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setId(UUID.randomUUID());
        metodoPagamento.setNome(tipo.getDescricao());
        metodoPagamento.setTipo(tipo);
        metodoPagamento.setExigeReferencia(exigeReferencia);
        metodoPagamento.setDiasLiquidacao(diasLiquidacao);
        metodoPagamento.setAtivo(ativo);
        return metodoPagamento;
    }

    public static MetodoPagamento dinheiroMetodoPagamento() {
        return metodoPagamento(TipoMetodoPagamentoEnum.DINHEIRO, false, 0, true);
    }

    public static MetodoPagamento agreementMetodoPagamento() {
        return metodoPagamento(TipoMetodoPagamentoEnum.CONVENIO, false, 0, true);
    }
}
