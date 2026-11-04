package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.fixture.BicoFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class BicoControllerIT extends OperacionalAIntegrationTest {

    private UUID bombaId;
    private UUID tanqueId;
    private UUID combustivelId;

    @BeforeEach
    void createPrerequisites() {
        combustivelId = createCombustivelProduto();
        tanqueId = createTanque(combustivelId);
        bombaId = createBomba();
    }

    @Test
    void shouldCreateBico() {
        request()
                .body(validoInput())
        .when()
                .post(BICOS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("codigo", equalTo(BicoFixture.CODE))
                .body("bombaId", equalTo(bombaId.toString()))
                .body("tanqueId", equalTo(tanqueId.toString()))
                .body("produtoId", equalTo(combustivelId.toString()))
                .body("numeroMedidor", equalTo(BicoFixture.MEDIDOR_NUMERO.floatValue()))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProdutoIsNotCombustivel() {
        BicoInputDTO input = validoInput();
        input.setProdutoId(createConvenienciaProduto());

        request().body(input).when().post(BICOS).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenProdutoDoesNotExist() {
        BicoInputDTO input = validoInput();
        input.setProdutoId(UUID.randomUUID());

        request().body(input).when().post(BICOS).then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsDuplicatedInSameBomba() {
        create(BICOS, validoInput());

        request().body(validoInput()).when().post(BICOS).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenBombaDoesNotExist() {
        BicoInputDTO input = validoInput();
        input.setBombaId(UUID.randomUUID());

        request().body(input).when().post(BICOS).then().statusCode(400);
    }

    @Test
    void shouldListAndFindBicos() {
        String id = create(BICOS, validoInput());

        request().when().get(BICOS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(BICOS + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("numeroMedidor", equalTo(BicoFixture.MEDIDOR_NUMERO.floatValue()));
    }

    @Test
    void shouldBuscaBicosByCode() {
        create(BICOS, validoInput());
        create(BICOS, validoInput(BicoFixture.OUTROS_CODE));

        request().queryParam("descricao", BicoFixture.OUTROS_CODE.toLowerCase()).when().get(BICOS + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].codigo", equalTo(BicoFixture.OUTROS_CODE));
        request().queryParam("descricao", "").when().get(BICOS + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateBico() {
        String id = create(BICOS, validoInput());
        BicoInputDTO update = validoInput(BicoFixture.OUTROS_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(BICOS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("codigo", equalTo(BicoFixture.OUTROS_CODE));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToNonCombustivelProduto() {
        String id = create(BICOS, validoInput());
        BicoInputDTO update = validoInput();
        update.setId(UUID.fromString(id));
        update.setProdutoId(createConvenienciaProduto());

        request().body(update).when().put(BICOS).then().statusCode(400);
    }

    @Test
    void shouldInactivateBicoOnDelete() {
        String id = create(BICOS, validoInput());

        request().when().delete(BICOS + "/{id}", id).then().statusCode(204);

        request().when().get(BICOS + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownBico() {
        request().when().get(BICOS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(BICOS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        BicoInputDTO update = validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(BICOS).then().statusCode(404);
    }

    private BicoInputDTO validoInput() {
        return BicoFixture.validoInput(bombaId, tanqueId, combustivelId);
    }

    private BicoInputDTO validoInput(String codigo) {
        BicoInputDTO input = validoInput();
        input.setCodigo(codigo);
        return input;
    }
}
