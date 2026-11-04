package com.br.manager.domain.operacional.support;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.fixture.BombaFixture;
import com.br.manager.domain.operacional.fixture.TanqueFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

/**
 * Base dos ITs de Produto/Tanque/Bomba/Bico: centraliza a criacao via API
 * dos cadastros pre-requisito.
 */
@Import(OperacionalADadosTeste.class)
public abstract class OperacionalAIntegrationTest extends AbstractIntegrationTest {

    protected static final String PRODUTOS = "/api/produtos";
    protected static final String PRODUTOS_ESTACAO = "/api/estacao-produtos";
    protected static final String TANQUES = "/api/tanques";
    protected static final String BOMBAS = "/api/bombas";
    protected static final String BICOS = "/api/bicos";

    @Autowired
    protected OperacionalADadosTeste testData;

    protected String createProduto(ProdutoInputDTO input) {
        return create(PRODUTOS, input);
    }

    protected UUID createCombustivelProduto() {
        return UUID.fromString(createProduto(ProdutoFixture.combustivelInput()));
    }

    protected UUID createConvenienciaProduto() {
        return UUID.fromString(createProduto(ProdutoFixture.convenienciaInput()));
    }

    protected String createTanque(TanqueInputDTO input) {
        return create(TANQUES, input);
    }

    protected UUID createTanque(UUID combustivelProdutoId) {
        return UUID.fromString(createTanque(TanqueFixture.validoInput(combustivelProdutoId)));
    }

    protected String createBomba(BombaInputDTO input) {
        return create(BOMBAS, input);
    }

    protected UUID createBomba() {
        return UUID.fromString(createBomba(BombaFixture.validoInput()));
    }

    protected String create(String path, Object input) {
        return request().body(input).when().post(path).then().statusCode(201).extract().path("id");
    }

    protected void delete(String path, String id) {
        request().when().delete(path + "/{id}", id).then().statusCode(204);
    }
}
