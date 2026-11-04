package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
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
class ItemRecebimentoMercadoriaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/item-recebimento-mercadoria";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID produtoId;
    private UUID tanqueId;
    private String mercadoriaReciboId;

    @BeforeEach
    void setUpPrerequisites() {
        produtoId = testData.createProduto();
        tanqueId = testData.createTanque(produtoId);
        mercadoriaReciboId = request()
                .body(toJson(RecebimentoMercadoriaFixture.validoInput(testData.createFornecedor(), testData.createUsuario(),
                        List.of(RecebimentoMercadoriaItemFixture.validoInput(null, produtoId, tanqueId)))))
        .when()
                .post("/recebimento-mercadoria")
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    void shouldCreateRecebimentoMercadoriaItem() {
        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("mercadoriaReciboId", equalTo(mercadoriaReciboId))
                .body("produtoId", equalTo(produtoId.toString()))
                .body("tanqueId", equalTo(tanqueId.toString()))
                .body("quantidade", comparesEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY))
                .body("custoUnitario", comparesEqualTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownRecebimentoMercadoria() {
        request()
                .body(toJson(RecebimentoMercadoriaItemFixture.validoInput(UUID.randomUUID(), produtoId, tanqueId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutQuantity() {
        ItemRecebimentoMercadoriaInputDTO input = validoInput();
        input.setQuantidade(null);

        request().body(toJson(input)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldSaveItensByRecebimentoMercadoria() {
        request()
                .body(toJson(List.of(validoInput(), validoInput())))
        .when()
                .post(PATH + "/recebimento-mercadoria/{mercadoriaReciboId}", mercadoriaReciboId)
        .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].mercadoriaReciboId", equalTo(mercadoriaReciboId));
    }

    @Test
    void shouldReturnNotFoundWhenSavingItensForUnknownRecebimentoMercadoria() {
        request()
                .body(toJson(List.of(validoInput())))
        .when()
                .post(PATH + "/recebimento-mercadoria/{mercadoriaReciboId}", UUID.randomUUID())
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListRecebimentoMercadoriaItens() {
        createItem();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldListItensByRecebimentoMercadoria() {
        createItem();

        request().when().get(PATH + "/por-recebimento-mercadoria/{id}", mercadoriaReciboId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].mercadoriaReciboId", equalTo(mercadoriaReciboId));
    }

    @Test
    void shouldFindRecebimentoMercadoriaItemById() {
        String id = createItem();

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownRecebimentoMercadoriaItem() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdateRecebimentoMercadoriaItem() {
        String id = createItem();
        ItemRecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setQuantidade(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("quantidade", comparesEqualTo(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownRecebimentoMercadoriaItem() {
        ItemRecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownRecebimentoMercadoria() {
        String id = createItem();
        ItemRecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setMercadoriaReciboId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteRecebimentoMercadoriaItem() {
        String id = createItem();

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownRecebimentoMercadoriaItem() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private ItemRecebimentoMercadoriaInputDTO validoInput() {
        return RecebimentoMercadoriaItemFixture.validoInput(UUID.fromString(mercadoriaReciboId), produtoId, tanqueId);
    }

    private String createItem() {
        return request()
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }
}
