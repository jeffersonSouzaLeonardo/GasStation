package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.entity.Bico;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Bico.
 */
public final class BicoFixture {

    public static final String CODE = "BICO-01";
    public static final String OUTROS_CODE = "BICO-02";
    public static final BigDecimal MEDIDOR_NUMERO = new BigDecimal("123456.5");

    private BicoFixture() {
    }

    public static BicoInputDTO validoInput(UUID bombaId, UUID tanqueId, UUID produtoId) {
        BicoInputDTO input = new BicoInputDTO();
        input.setBombaId(bombaId);
        input.setTanqueId(tanqueId);
        input.setProdutoId(produtoId);
        input.setCodigo(CODE);
        input.setNumeroMedidor(MEDIDOR_NUMERO);
        input.setAtivo(true);
        return input;
    }

    public static BicoInputDTO validoInput() {
        return validoInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static Bico persistedEntity() {
        Bico bico = new Bico();
        bico.setId(UUID.randomUUID());
        bico.setBombaId(UUID.randomUUID());
        bico.setTanqueId(UUID.randomUUID());
        bico.setProdutoId(UUID.randomUUID());
        bico.setCodigo(CODE);
        bico.setNumeroMedidor(MEDIDOR_NUMERO);
        bico.setAtivo(true);
        return bico;
    }
}
