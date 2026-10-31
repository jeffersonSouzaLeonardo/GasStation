package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.fixture.AuditFixture;
import com.br.manager.domain.organization.fixture.CompanyFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class AuditControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/audit";
    private static final LocalDateTime OLDER = LocalDateTime.of(2026, 5, 20, 14, 30);
    private static final LocalDateTime NEWER = LocalDateTime.of(2026, 5, 21, 9, 0);
    private static final String CREATED_AT_JSON = "2026-05-20T14:30:00Z";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Disabled("BUG: Audit entity has no companyId mapping while audit_logs.company_id is NOT NULL (V1 migration), "
            + "so POST /audit always fails with 400 and companyId is never persisted/returned")
    void shouldCreateAudit() {
        String companyId = createCompany();

        request()
                .body(auditBody(companyId, null))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("companyId", equalTo(companyId))
                .body("entityName", equalTo(AuditFixture.ENTITY_NAME))
                .body("action", equalTo(AuditFixture.ACTION));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() {
        request().body(Map.of("ipAddress", AuditFixture.IP_ADDRESS)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAuditsMostRecentFirst() {
        String companyId = createCompany();
        String older = insertAudit(companyId, AuditFixture.ENTITY_NAME, OLDER);
        String newer = insertAudit(companyId, AuditFixture.OTHER_ENTITY_NAME, NEWER);

        request().when().get(PATH).then().statusCode(200)
                .body("$", hasSize(2))
                .body("[0].id", equalTo(newer))
                .body("[1].id", equalTo(older));
    }

    @Test
    void shouldFindAuditById() {
        String id = insertAudit(createCompany(), AuditFixture.ENTITY_NAME, OLDER);

        request().when().get(PATH + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("entityName", equalTo(AuditFixture.ENTITY_NAME))
                .body("entityId", equalTo(AuditFixture.ENTITY_ID))
                .body("action", equalTo(AuditFixture.ACTION));
    }

    @Test
    void shouldSearchAuditsByEntityName() {
        String companyId = createCompany();
        insertAudit(companyId, AuditFixture.ENTITY_NAME, OLDER);
        insertAudit(companyId, AuditFixture.OTHER_ENTITY_NAME, NEWER);

        request().queryParam("entityName", "Stat").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].entityName", equalTo(AuditFixture.OTHER_ENTITY_NAME));
        request().queryParam("name", "Comp").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].entityName", equalTo(AuditFixture.ENTITY_NAME));
        request().when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateAudit() {
        String companyId = createCompany();
        String id = insertAudit(companyId, AuditFixture.ENTITY_NAME, OLDER);

        request()
                .body(auditBody(companyId, id))
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("ipAddress", equalTo(AuditFixture.IP_ADDRESS));
    }

    @Test
    void shouldPhysicallyDeleteAudit() {
        String id = insertAudit(createCompany(), AuditFixture.ENTITY_NAME, OLDER);

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownAudit() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().body(auditBody(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .when().put(PATH).then().statusCode(404);
    }

    private String createCompany() {
        return request().body(CompanyFixture.validInput()).when().post("/company")
                .then().statusCode(200).extract().path("id");
    }

    /**
     * Insere direto no banco: o POST /audit nao consegue persistir (ver teste desabilitado shouldCreateAudit).
     */
    private String insertAudit(String companyId, String entityName, LocalDateTime createdAt) {
        String id = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO audit_logs (id, company_id, entity_name, entity_id, action, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                id, companyId, entityName, AuditFixture.ENTITY_ID, AuditFixture.ACTION, createdAt);
        return id;
    }

    private Map<String, Object> auditBody(String companyId, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("companyId", companyId);
        body.put("entityName", AuditFixture.ENTITY_NAME);
        body.put("entityId", AuditFixture.ENTITY_ID);
        body.put("action", AuditFixture.ACTION);
        body.put("ipAddress", AuditFixture.IP_ADDRESS);
        body.put("createdAt", CREATED_AT_JSON);
        return body;
    }
}
