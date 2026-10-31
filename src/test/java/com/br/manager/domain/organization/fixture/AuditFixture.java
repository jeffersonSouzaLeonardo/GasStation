package com.br.manager.domain.organization.fixture;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.entity.Audit;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Auditoria.
 */
public final class AuditFixture {

    public static final String ENTITY_NAME = "Company";
    public static final String OTHER_ENTITY_NAME = "Station";
    public static final String ENTITY_ID = "4f1c2d3e-0000-0000-0000-000000000001";
    public static final String ACTION = "UPDATE";
    public static final String BEFORE_DATA = "{\"name\": \"antes\"}";
    public static final String AFTER_DATA = "{\"name\": \"depois\"}";
    public static final String IP_ADDRESS = "192.168.0.10";
    public static final OffsetDateTime CREATED_AT = OffsetDateTime.of(2026, 5, 20, 14, 30, 0, 0, ZoneOffset.UTC);

    private AuditFixture() {
    }

    public static AuditInputDTO validInput(UUID companyId) {
        AuditInputDTO input = new AuditInputDTO();
        input.setCompanyId(companyId);
        input.setEntityName(ENTITY_NAME);
        input.setEntityId(ENTITY_ID);
        input.setAction(ACTION);
        input.setBeforeData(BEFORE_DATA);
        input.setAfterData(AFTER_DATA);
        input.setIpAddress(IP_ADDRESS);
        input.setCreatedAt(CREATED_AT);
        return input;
    }

    public static AuditInputDTO validInput() {
        return validInput(UUID.randomUUID());
    }

    public static Audit persistedEntity() {
        Audit audit = new Audit();
        audit.setId(UUID.randomUUID());
        audit.setUserId(UUID.randomUUID());
        audit.setEntityName(ENTITY_NAME);
        audit.setEntityId(ENTITY_ID);
        audit.setAction(ACTION);
        audit.setBeforeData(BEFORE_DATA);
        audit.setAfterData(AFTER_DATA);
        audit.setIpAddress(IP_ADDRESS);
        audit.setCreatedAt(CREATED_AT);
        return audit;
    }
}
