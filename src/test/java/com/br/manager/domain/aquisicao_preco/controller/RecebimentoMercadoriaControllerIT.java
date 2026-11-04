package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
import com.br.manager.domain.aquisicao_preco.support.AquisicaoPrecoDadosTeste;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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
class RecebimentoMercadoriaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/recebimento-mercadoria";

    @Autowired
    private AquisicaoPrecoDadosTeste testData;

    private UUID fornecedorId;
    private UUID usuarioId;
    private UUID produtoId;
    private UUID tanqueId;

    @BeforeEach
    void setUpPrerequisites() {
        fornecedorId = testData.createFornecedor();
        usuarioId = testData.createUsuario();
        produtoId = testData.createProduto();
        tanqueId = testData.createTanque(produtoId);
    }

    @Test
    void shouldCreateRecebimentoMercadoriaWithItens() {
        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(validoInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("fornecedorId", equalTo(fornecedorId.toString()))
                .body("recebidoPor", equalTo(usuarioId.toString()))
                .body("numeroNota", equalTo(RecebimentoMercadoriaFixture.NUMERO_NOTA))
                .body("chaveNota", equalTo(RecebimentoMercadoriaFixture.CHAVE_NOTA))
                .body("recebidoEm", equalTo("2026-01-10T15:45:00"))
                .body("status", equalTo(RecebimentoMercadoria.Situacao.RECEBIDO))
                .body("valorFrete", comparesEqualTo(RecebimentoMercadoriaFixture.VALOR_FRETE))
                .body("outrosCustos", comparesEqualTo(RecebimentoMercadoriaFixture.OUTROS_CUSTOS))
                .body("itens", hasSize(1))
                .body("itens[0].produtoId", equalTo(produtoId.toString()))
                .body("itens[0].tanqueId", equalTo(tanqueId.toString()))
                .body("itens[0].quantidade", comparesEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY));
    }

    @Test
    void shouldCreateRecebimentoMercadoriaAsDraftWhenSituacaoIsVazio() {
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setStatus(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("status", equalTo(RecebimentoMercadoria.Situacao.DRAFT));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutItens() {
        request()
                .body(toJson(RecebimentoMercadoriaFixture.validoInput(fornecedorId, usuarioId, List.of())))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O recebimento de mercadorias deve conter pelo menos um item."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutNotaKey() {
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setChaveNota(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("A chave da nota fiscal é obrigatória."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInativoFornecedor() {
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setFornecedorId(testData.createInativoFornecedor());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInativoUsuario() {
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setRecebidoPor(testData.createInativoUsuario());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownPedidoCompra() {
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setCompraOrderId(UUID.randomUUID());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldListRecebimentoMercadorias() {
        createRecebimentoMercadoria(validoInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListRecebimentoMercadoriasByFornecedor() {
        createRecebimentoMercadoria(validoInput());

        request().when().get(PATH + "/por-fornecedor/{id}", fornecedorId)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].fornecedorId", equalTo(fornecedorId.toString()));
        request().when().get(PATH + "/por-fornecedor/{id}", UUID.randomUUID())
                .then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldListRecebimentoMercadoriasByPedidoCompra() {
        String compraOrderId = createPedidoCompra();
        RecebimentoMercadoriaInputDTO input = validoInput();
        input.setCompraOrderId(UUID.fromString(compraOrderId));
        createRecebimentoMercadoria(input);

        request().when().get(PATH + "/por-pedido-compra/{id}", compraOrderId)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].compraOrderId", equalTo(compraOrderId));
    }

    @Test
    void shouldFindRecebimentoMercadoriaById() {
        String id = createRecebimentoMercadoria(validoInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("itens", hasSize(1));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownRecebimentoMercadoria() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdateRecebimentoMercadoriaAndReplaceItens() {
        String id = createRecebimentoMercadoria(validoInput());
        RecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setNumeroNota(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
        update.getItens().get(0).setQuantidade(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);

        request()
                .config(AquisicaoPrecoDadosTeste.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("numeroNota", equalTo(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO))
                .body("itens", hasSize(1))
                .body("itens[0].quantidade", comparesEqualTo(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutId() {
        request()
                .body(toJson(validoInput()))
        .when()
                .patch(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do recebimento de mercadorias é obrigatório para atualização."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownRecebimentoMercadoria() {
        RecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownFornecedor() {
        String id = createRecebimentoMercadoria(validoInput());
        RecebimentoMercadoriaInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setFornecedorId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteRecebimentoMercadoria() {
        String id = createRecebimentoMercadoria(validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownRecebimentoMercadoria() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private RecebimentoMercadoriaInputDTO validoInput() {
        return RecebimentoMercadoriaFixture.validoInput(fornecedorId, usuarioId,
                new ArrayList<>(List.of(RecebimentoMercadoriaItemFixture.validoInput(null, produtoId, tanqueId))));
    }

    private String createRecebimentoMercadoria(RecebimentoMercadoriaInputDTO input) {
        return request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    private String createPedidoCompra() {
        return request()
                .body(toJson(PedidoCompraFixture.validoInput(fornecedorId, usuarioId,
                        List.of(PedidoCompraItemFixture.validoInput(null, produtoId)))))
        .when()
                .post("/pedido-compra")
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
