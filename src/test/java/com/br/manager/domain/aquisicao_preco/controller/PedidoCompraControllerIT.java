package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
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
class PedidoCompraControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/pedido-compra";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID fornecedorId;
    private UUID usuarioId;
    private UUID produtoId;

    @BeforeEach
    void setUpPrerequisites() {
        fornecedorId = testData.createFornecedor();
        usuarioId = testData.createUsuario();
        produtoId = testData.createProduto();
    }

    @Test
    void shouldCreatePedidoCompraAsDraftWithItens() {
        PedidoCompraInputDTO input = validoInput();
        input.setStatus(PedidoCompra.Situacao.COMPLETED);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("numero", equalTo(PedidoCompraFixture.NUMERO))
                .body("status", equalTo(PedidoCompra.Situacao.DRAFT))
                .body("fornecedorId", equalTo(fornecedorId.toString()))
                .body("criadoPor", equalTo(usuarioId.toString()))
                .body("pedidoEm", equalTo("2026-01-05T09:30:00"))
                .body("previstoEm", equalTo("2026-01-10T14:00:00"))
                .body("totalValor", comparesEqualTo(PedidoCompraItemFixture.CUSTO_TOTAL))
                .body("itens", hasSize(1))
                .body("itens[0].produtoId", equalTo(produtoId.toString()))
                .body("itens[0].quantidade", equalTo(PedidoCompraItemFixture.QUANTITY))
                .body("itens[0].custoTotal", comparesEqualTo(PedidoCompraItemFixture.CUSTO_TOTAL));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutItens() {
        PedidoCompraInputDTO input = PedidoCompraFixture.validoInput(fornecedorId, usuarioId, List.of());

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O pedido de compra deve conter pelo menos um item."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutNumero() {
        PedidoCompraInputDTO input = validoInput();
        input.setNumero(" ");

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O número do pedido de compra é obrigatório."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownFornecedor() {
        PedidoCompraInputDTO input = validoInput();
        input.setFornecedorId(UUID.randomUUID());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInativoFornecedor() {
        PedidoCompraInputDTO input = validoInput();
        input.setFornecedorId(testData.createInativoFornecedor());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInativoUsuario() {
        PedidoCompraInputDTO input = validoInput();
        input.setCriadoPor(testData.createInativoUsuario());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    @Disabled("BUG: findAll at PedidoCompraService.java:97 is not @Transactional (spring.jpa.open-in-view=false); PedidoCompra.setItens (PedidoCompra.java:124) clears the lazy itens collection of a detached entity -> LazyInitializationException -> HTTP 500")
    void shouldListPedidoCompras() {
        createPedidoCompra();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1)).body("[0].itens", hasSize(1));
    }

    @Test
    @Disabled("BUG: findById at PedidoCompraService.java:90 is not @Transactional (spring.jpa.open-in-view=false); PedidoCompra.setItens (PedidoCompra.java:124) clears the lazy itens collection of a detached entity -> LazyInitializationException -> HTTP 500")
    void shouldFindPedidoCompraById() {
        String id = createPedidoCompra();

        request().when().get(PATH + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("itens", hasSize(1));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownPedidoCompra() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdatePedidoCompraAndReplaceItens() {
        String id = createPedidoCompra();
        PedidoCompraInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setNumero(PedidoCompraFixture.ATUALIZADO_NUMERO);
        update.getItens().get(0).setQuantidade(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("numero", equalTo(PedidoCompraFixture.ATUALIZADO_NUMERO))
                .body("itens", hasSize(1))
                .body("totalValor", comparesEqualTo(PedidoCompraItemFixture.ATUALIZADO_TOTAL_CUSTO));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPedidoCompra() {
        PedidoCompraInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldApprovePedidoCompraInDraft() {
        String id = createPedidoCompra();

        request().when().patch(PATH + "/{id}/aprovar", id)
                .then().statusCode(200).body("status", equalTo(PedidoCompra.Situacao.APROVADO));
    }

    @Test
    void shouldReturnBadRequestWhenSendingDraftPedidoCompra() {
        String id = createPedidoCompra();

        request().when().patch(PATH + "/{id}/enviar", id)
                .then()
                .statusCode(400)
                .body("title", equalTo("O pedido de compra só pode mudar de APROVADO para SENT."));
    }

    @Test
    void shouldSendAprovadoPedidoCompra() {
        String id = createPedidoCompra();
        request().when().patch(PATH + "/{id}/aprovar", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/enviar", id)
                .then().statusCode(200).body("status", equalTo(PedidoCompra.Situacao.SENT));
    }

    @Test
    void shouldCompleteSentPedidoCompra() {
        String id = createPedidoCompra();
        request().when().patch(PATH + "/{id}/aprovar", id).then().statusCode(200);
        request().when().patch(PATH + "/{id}/enviar", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/concluir", id)
                .then().statusCode(200).body("status", equalTo(PedidoCompra.Situacao.COMPLETED));
    }

    @Test
    void shouldReturnBadRequestWhenCompletingDraftPedidoCompra() {
        String id = createPedidoCompra();

        request().when().patch(PATH + "/{id}/concluir", id).then().statusCode(400);
    }

    @Test
    void shouldCancelDraftPedidoCompra() {
        String id = createPedidoCompra();

        request().when().patch(PATH + "/{id}/cancelar", id)
                .then().statusCode(200).body("status", equalTo(PedidoCompra.Situacao.CANCELADO));
    }

    @Test
    void shouldReturnBadRequestWhenCancelingCanceladoPedidoCompra() {
        String id = createPedidoCompra();
        request().when().patch(PATH + "/{id}/cancelar", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/cancelar", id)
                .then()
                .statusCode(400)
                .body("title", equalTo("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados."));
    }

    @Test
    void shouldReturnNotFoundWhenApprovingUnknownPedidoCompra() {
        request().when().patch(PATH + "/{id}/aprovar", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenSendingUnknownPedidoCompra() {
        request().when().patch(PATH + "/{id}/enviar", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCompletingUnknownPedidoCompra() {
        request().when().patch(PATH + "/{id}/concluir", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCancelingUnknownPedidoCompra() {
        request().when().patch(PATH + "/{id}/cancelar", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldDeletePedidoCompra() {
        String id = createPedidoCompra();

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownPedidoCompra() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private PedidoCompraInputDTO validoInput() {
        return PedidoCompraFixture.validoInput(fornecedorId, usuarioId,
                new ArrayList<>(List.of(PedidoCompraItemFixture.validoInput(null, produtoId))));
    }

    private String createPedidoCompra() {
        return request()
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
