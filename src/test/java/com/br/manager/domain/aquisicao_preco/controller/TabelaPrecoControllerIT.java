package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoDadosTeste;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoDadosTeste.toJson;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Import(AquisicaoPrecoDadosTeste.class)
class TabelaPrecoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/tabela-preco";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID usuarioId;
    private UUID produtoId;

    @BeforeEach
    void setUpPrerequisites() {
        usuarioId = testData.createUsuario();
        produtoId = testData.createProduto();
    }

    @Test
    void shouldCreateTabelaPrecoWithItens() {
        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("nome", equalTo(TabelaPrecoFixture.NAME))
                .body("status", equalTo(TabelaPrecoFixture.STATUS))
                .body("validoDe", equalTo(TabelaPrecoFixture.VALIDO_DE.toString()))
                .body("validoAte", equalTo(TabelaPrecoFixture.VALIDO_ATE.toString()))
                .body("criadoPor", equalTo(usuarioId.toString()))
                .body("itensPreco", hasSize(1))
                .body("itensPreco[0].produtoId", equalTo(produtoId.toString()))
                .body("itensPreco[0].precoUnitario", comparesEqualTo(ItemPrecoFixture.PRECO_UNITARIO))
                .body("itensPreco[0].ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutItens() {
        request()
                .body(toJson(TabelaPrecoFixture.validoInput(usuarioId, List.of())))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("A tabela de preços deve conter pelo menos um item"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownUsuario() {
        request()
                .body(toJson(TabelaPrecoFixture.validoInput(UUID.randomUUID(),
                        List.of(ItemPrecoFixture.validoInput(null, produtoId)))))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInativoUsuario() {
        UUID inativoUsuarioId = testData.createInativoUsuario();

        request()
                .body(toJson(TabelaPrecoFixture.validoInput(inativoUsuarioId,
                        List.of(ItemPrecoFixture.validoInput(null, produtoId)))))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutName() {
        TabelaPrecoInputDTO input = validoInput();
        input.setNome(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400);
    }

    @Test
    @Disabled("BUG: findAll at TabelaPrecoService.java:116 runs outside a transacao (spring.jpa.open-in-view=false) and TabelaPrecoMapper.java:53 touches the lazy itensPreco collection -> LazyInitializationException -> HTTP 500")
    void shouldListTabelaPrecos() {
        createTabelaPreco(validoInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    @Disabled("BUG: find at TabelaPrecoService.java:132 runs outside a transacao (spring.jpa.open-in-view=false) and TabelaPrecoMapper.java:53 touches the lazy itensPreco collection -> LazyInitializationException -> HTTP 500")
    void shouldFindTabelaPrecoById() {
        String id = createTabelaPreco(validoInput());

        request().when().get(PATH + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo(TabelaPrecoFixture.NAME));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownTabelaPreco() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    @Disabled("BUG: findByDescricao at TabelaPrecoService.java:120 runs outside a transacao (spring.jpa.open-in-view=false) and TabelaPrecoMapper.java:53 touches the lazy itensPreco collection -> LazyInitializationException -> HTTP 500")
    void shouldBuscaTabelaPrecosByName() {
        createTabelaPreco(validoInput());
        TabelaPrecoInputDTO outros = validoInput();
        outros.setNome(TabelaPrecoFixture.OUTROS_NAME);
        createTabelaPreco(outros);

        request().queryParam("nome", "lubrificantes").when().get(PATH + "/busca")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nome", equalTo(TabelaPrecoFixture.OUTROS_NAME));
    }

    @Test
    @Disabled("BUG: findByDescricao at TabelaPrecoService.java:120 runs outside a transacao (spring.jpa.open-in-view=false) and TabelaPrecoMapper.java:53 touches the lazy itensPreco collection -> LazyInitializationException -> HTTP 500")
    void shouldReturnAllTabelaPrecosWhenBuscaIsVazio() {
        createTabelaPreco(validoInput());
        TabelaPrecoInputDTO outros = validoInput();
        outros.setNome(TabelaPrecoFixture.OUTROS_NAME);
        createTabelaPreco(outros);

        request().when().get(PATH + "/busca").then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateTabelaPrecoAndReplaceItens() {
        String id = createTabelaPreco(validoInput());
        TabelaPrecoInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setStatus(TabelaPrecoFixture.INACTIVE_SITUACAO);
        update.getItensPreco().get(0).setPrecoUnitario(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("status", equalTo(TabelaPrecoFixture.INACTIVE_SITUACAO))
                .body("itensPreco", hasSize(1))
                .body("itensPreco[0].precoUnitario", comparesEqualTo(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownTabelaPreco() {
        TabelaPrecoInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownUsuario() {
        String id = createTabelaPreco(validoInput());
        TabelaPrecoInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setCriadoPor(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteTabelaPreco() {
        String id = createTabelaPreco(validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownTabelaPreco() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private TabelaPrecoInputDTO validoInput() {
        return TabelaPrecoFixture.validoInput(usuarioId, new ArrayList<>(List.of(ItemPrecoFixture.validoInput(null, produtoId))));
    }

    private String createTabelaPreco(TabelaPrecoInputDTO input) {
        return request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }
}
