package com.br.manager.domain.organizacao.fixture;

import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.entity.Auditoria;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Auditoria.
 */
public final class AuditoriaFixture {

    public static final String ENTITY_NAME = "Empresa";
    public static final String OUTROS_ENTITY_NAME = "Estacao";
    public static final String ENTITY_ID = "4f1c2d3e-0000-0000-0000-000000000001";
    public static final String ACAO = "UPDATE";
    public static final String BEFORE_DATA = "{\"nome\": \"antes\"}";
    public static final String AFTER_DATA = "{\"nome\": \"depois\"}";
    public static final String IP_ENDERECO = "192.168.0.10";
    public static final OffsetDateTime CRIADO_EM = OffsetDateTime.of(2026, 5, 20, 14, 30, 0, 0, ZoneOffset.UTC);

    private AuditoriaFixture() {
    }

    public static AuditoriaInputDTO validoInput(UUID empresaId) {
        AuditoriaInputDTO input = new AuditoriaInputDTO();
        input.setEmpresaId(empresaId);
        input.setEntityName(ENTITY_NAME);
        input.setEntityId(ENTITY_ID);
        input.setAcao(ACAO);
        input.setBeforeData(BEFORE_DATA);
        input.setAfterData(AFTER_DATA);
        input.setEnderecoIp(IP_ENDERECO);
        input.setCriadoEm(CRIADO_EM);
        return input;
    }

    public static AuditoriaInputDTO validoInput() {
        return validoInput(UUID.randomUUID());
    }

    public static Auditoria persistedEntity() {
        Auditoria auditoria = new Auditoria();
        auditoria.setId(UUID.randomUUID());
        auditoria.setUsuarioId(UUID.randomUUID());
        auditoria.setEntityName(ENTITY_NAME);
        auditoria.setEntityId(ENTITY_ID);
        auditoria.setAcao(ACAO);
        auditoria.setBeforeData(BEFORE_DATA);
        auditoria.setAfterData(AFTER_DATA);
        auditoria.setEnderecoIp(IP_ENDERECO);
        auditoria.setCriadoEm(CRIADO_EM);
        return auditoria;
    }
}
