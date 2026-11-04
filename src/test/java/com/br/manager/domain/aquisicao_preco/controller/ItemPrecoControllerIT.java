package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoDadosTeste;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoDadosTeste.toJson;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Import(AquisicaoPrecoDadosTeste.class)
class ItemPrecoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/item-preco";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID produtoId;
    private String precoTabelaId;

    @BeforeEach
    void setUpPrerequisites() {
        UUID usuarioId = testData.createUsuario();
        produtoId = testData.createProduto();
        precoTabelaId = request()
                .body(toJson(TabelaPrecoFixture.validoInput(usuarioId, List.of(ItemPrecoFixture.validoInput(null, produtoId)))))
        .when()
                .post("/tabela-preco")
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    void shouldCreateItemPreco() {
        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("precoTabelaId", equalTo(precoTabelaId))
                .body("produtoId", equalTo(produtoId.toString()))
                .body("precoUnitario", comparesEqualTo(ItemPrecoFixture.PRECO_UNITARIO))
                .body("precoMinimo", comparesEqualTo(ItemPrecoFixture.PRECO_MINIMO))
                .body("descontoMaximoPercentual", comparesEqualTo(ItemPrecoFixture.DESCONTO_MAXIMO_PERCENTUAL))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownTabelaPreco() {
        request()
                .body(toJson(ItemPrecoFixture.validoInput(UUID.randomUUID(), produtoId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutUnidadePreco() {
        ItemPrecoInputDTO input = validoInput();
        input.setPrecoUnitario(null);

        request().body(toJson(input)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldSaveItensByTabelaPreco() {
        request()
                .body(toJson(List.of(validoInput(), validoInput())))
        .when()
                .post(PATH + "/tabela-preco/{precoTabelaId}", precoTabelaId)
        .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].precoTabelaId", equalTo(precoTabelaId));
    }

    @Test
    void shouldReturnNotFoundWhenSavingItensForUnknownTabelaPreco() {
        request()
                .body(toJson(List.of(validoInput())))
        .when()
                .post(PATH + "/tabela-preco/{precoTabelaId}", UUID.randomUUID())
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListAtivoItensOnly() {
        ItemPrecoInputDTO inativo = validoInput();
        inativo.setAtivo(false);
        createItemPreco(inativo);

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListItensByTabelaPreco() {
        createItemPreco(validoInput());

        request().when().get(PATH + "/por-tabela-preco/{id}", precoTabelaId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].precoTabelaId", equalTo(precoTabelaId));
    }

    @Test
    void shouldFindItemPrecoById() {
        String id = createItemPreco(validoInput());

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingInativoItemPreco() {
        ItemPrecoInputDTO inativo = validoInput();
        inativo.setAtivo(false);
        String id = createItemPreco(inativo);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownItemPreco() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdateItemPreco() {
        String id = createItemPreco(validoInput());
        ItemPrecoInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setPrecoUnitario(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("precoUnitario", comparesEqualTo(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownItemPreco() {
        ItemPrecoInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownTabelaPreco() {
        String id = createItemPreco(validoInput());
        ItemPrecoInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setPrecoTabelaId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteItemPreco() {
        String id = createItemPreco(validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownItemPreco() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private ItemPrecoInputDTO validoInput() {
        return ItemPrecoFixture.validoInput(UUID.fromString(precoTabelaId), produtoId);
    }

    private String createItemPreco(ItemPrecoInputDTO input) {
        return request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }
}
