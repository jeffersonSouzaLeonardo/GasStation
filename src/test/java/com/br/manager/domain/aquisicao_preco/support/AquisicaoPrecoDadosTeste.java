package com.br.manager.domain.aquisicao_preco.support;

import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cria, via repositorios, os pre-requisitos de outros dominios (usuario, fornecedor,
 * produto e tanque) necessarios aos testes de integracao de precos e aquisicao.
 */
@TestComponent
public class AquisicaoPrecoDadosTeste {

    /** Faz o RestAssured ler numeros JSON como BigDecimal, permitindo comparar valores monetarios sem perda. */
    public static final RestAssuredConfig DECIMAL_CONFIG = RestAssuredConfig.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private TanqueRepository tanqueRepository;

    /** Serializa DTOs com datas ISO (o ObjectMapper padrao do RestAssured nao conhece java.time). */
    public static String toJson(Object body) {
        try {
            return JSON.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public UUID createUsuario() {
        return createUsuario(true);
    }

    public UUID createInativoUsuario() {
        return createUsuario(false);
    }

    public UUID createFornecedor() {
        return createFornecedor(true);
    }

    public UUID createInativoFornecedor() {
        return createFornecedor(false);
    }

    public UUID createProduto() {
        Produto produto = new Produto();
        produto.setSku("GAS-" + UUID.randomUUID().toString().substring(0, 8));
        produto.setNome("Gasolina Comum");
        produto.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        produto.setUnidade(TipoUnidadeEnum.L);
        produto.setAtivo(true);
        return produtoRepository.saveAndFlush(produto).getId();
    }

    public UUID createTanque(UUID produtoId) {
        Tanque tanque = new Tanque();
        tanque.setCodigo("TQ-" + UUID.randomUUID().toString().substring(0, 8));
        tanque.setProdutoId(produtoId);
        tanque.setCapacidadeLitros(new BigDecimal("30000.0000"));
        tanque.setAtivo(true);
        return tanqueRepository.saveAndFlush(tanque).getId();
    }

    private UUID createUsuario(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Comprador Teste");
        usuario.setEmail("comprador-" + UUID.randomUUID() + "@posto.com.br");
        usuario.setPasswordHash("hash");
        usuario.setAtivo(ativo);
        return usuarioRepository.saveAndFlush(usuario).getId();
    }

    private UUID createFornecedor(boolean ativo) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setLegalName("Distribuidora Teste SA");
        fornecedor.setCnpjCpf("12345678000190");
        fornecedor.setAtivo(ativo);
        return fornecedorRepository.saveAndFlush(fornecedor).getId();
    }
}
