package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
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
class ItemPedidoCompraControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/item-pedido-compra";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID produtoId;
    private String compraOrderId;

    @BeforeEach
    void setUpPrerequisites() {
        produtoId = testData.createProduto();
        compraOrderId = request()
                .body(toJson(PedidoCompraFixture.validoInput(testData.createFornecedor(), testData.createUsuario(),
                        List.of(PedidoCompraItemFixture.validoInput(null, produtoId)))))
        .when()
                .post("/pedido-compra")
        .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    void shouldCreatePedidoCompraItem() {
        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("compraOrderId", equalTo(compraOrderId))
                .body("produtoId", equalTo(produtoId.toString()))
                .body("custoUnitario", comparesEqualTo(PedidoCompraItemFixture.CUSTO_UNITARIO))
                .body("custoTotal", comparesEqualTo(PedidoCompraItemFixture.CUSTO_TOTAL));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithZeroQuantity() {
        ItemPedidoCompraInputDTO input = validoInput();
        input.setQuantidade(0);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("A quantidade deve ser maior que zero."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutProduto() {
        ItemPedidoCompraInputDTO input = validoInput();
        input.setProdutoId(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do produto é obrigatório."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownPedidoCompra() {
        request()
                .body(toJson(PedidoCompraItemFixture.validoInput(UUID.randomUUID(), produtoId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListPedidoCompraItens() {
        createItem();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldListItensByPedidoCompra() {
        createItem();

        request().when().get(PATH + "/por-pedido-compra/{id}", compraOrderId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].compraOrderId", equalTo(compraOrderId));
    }

    @Test
    void shouldFindPedidoCompraItemById() {
        String id = createItem();

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownPedidoCompraItem() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdatePedidoCompraItem() {
        String id = createItem();
        ItemPedidoCompraInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setQuantidade(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("quantidade", equalTo(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA))
                .body("custoTotal", comparesEqualTo(PedidoCompraItemFixture.ATUALIZADO_TOTAL_CUSTO));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutId() {
        request()
                .body(toJson(validoInput()))
        .when()
                .patch(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do item do pedido de compra é obrigatório para atualização."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPedidoCompraItem() {
        ItemPedidoCompraInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeletePedidoCompraItem() {
        String id = createItem();

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownPedidoCompraItem() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private ItemPedidoCompraInputDTO validoInput() {
        return PedidoCompraItemFixture.validoInput(UUID.fromString(compraOrderId), produtoId);
    }

    private String createItem() {
        return request()
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
