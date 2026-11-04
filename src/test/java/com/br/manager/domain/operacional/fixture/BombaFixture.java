package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.entity.Bomba;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Bomba.
 */
public final class BombaFixture {

    public static final String CODE = "BOMBA-01";
    public static final String OUTROS_CODE = "BOMBA-02";
    public static final String LOCATION = "Ilha 1";
    public static final String TOO_LONG_CODE = "B".repeat(51);

    private BombaFixture() {
    }

    public static BombaInputDTO validoInput() {
        BombaInputDTO input = new BombaInputDTO();
        input.setCodigo(CODE);
        input.setLocalizacao(LOCATION);
        input.setAtivo(true);
        return input;
    }

    public static BombaInputDTO validoInput(String codigo) {
        BombaInputDTO input = validoInput();
        input.setCodigo(codigo);
        return input;
    }

    public static Bomba persistedEntity() {
        Bomba bomba = new Bomba();
        bomba.setId(UUID.randomUUID());
        bomba.setCodigo(CODE);
        bomba.setLocalizacao(LOCATION);
        bomba.setAtivo(true);
        return bomba;
    }
}
