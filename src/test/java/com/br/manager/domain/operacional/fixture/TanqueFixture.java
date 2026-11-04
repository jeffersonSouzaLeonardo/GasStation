package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.entity.Tanque;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Tanque.
 */
public final class TanqueFixture {

    public static final String CODE = "TQ-01";
    public static final String OUTROS_CODE = "TQ-02";
    public static final BigDecimal CAPACIDADE = new BigDecimal("15000.5");
    public static final BigDecimal MORTO_ESTOQUE = new BigDecimal("150.25");
    public static final BigDecimal CURRENT_CONTABIL = new BigDecimal("8000.75");

    private TanqueFixture() {
    }

    public static TanqueInputDTO validoInput(UUID produtoId) {
        TanqueInputDTO input = new TanqueInputDTO();
        input.setCodigo(CODE);
        input.setProdutoId(produtoId);
        input.setCapacidadeLitros(CAPACIDADE);
        input.setEstoqueMortoLitros(MORTO_ESTOQUE);
        input.setSaldoContabilLitros(CURRENT_CONTABIL);
        input.setAtivo(true);
        return input;
    }

    public static TanqueInputDTO validoInput(UUID produtoId, String codigo) {
        TanqueInputDTO input = validoInput(produtoId);
        input.setCodigo(codigo);
        return input;
    }

    public static Tanque persistedEntity() {
        Tanque tanque = new Tanque();
        tanque.setId(UUID.randomUUID());
        tanque.setCodigo(CODE);
        tanque.setProdutoId(UUID.randomUUID());
        tanque.setCapacidadeLitros(CAPACIDADE);
        tanque.setEstoqueMortoLitros(MORTO_ESTOQUE);
        tanque.setSaldoContabilLitros(CURRENT_CONTABIL);
        tanque.setAtivo(true);
        return tanque;
    }
}
