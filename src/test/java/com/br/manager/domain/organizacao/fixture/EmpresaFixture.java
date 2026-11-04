package com.br.manager.domain.organizacao.fixture;

import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.entity.Empresa;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Empresa.
 */
public final class EmpresaFixture {

    public static final String LEGAL_NAME = "Posto Bom Combustivel LTDA";
    public static final String TRADE_NAME = "Posto Bom";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CNPJ = "12345678000190";
    public static final String MASKED_PHONE = "(11) 98765-4321";
    public static final String PHONE = "11987654321";

    private EmpresaFixture() {
    }

    public static EmpresaInputDTO validoInput() {
        EmpresaInputDTO input = new EmpresaInputDTO();
        input.setLegalName(LEGAL_NAME);
        input.setTradeName(TRADE_NAME);
        input.setCnpj(MASKED_CNPJ);
        input.setInscricaoEstadual("123.456.789");
        input.setTaxRegime("SIMPLES_NACIONAL");
        input.setEmail("contato@postobom.com.br");
        input.setTelefone(MASKED_PHONE);
        input.setAtivo(true);
        return input;
    }

    public static EmpresaInputDTO validoInput(String cnpj) {
        EmpresaInputDTO input = validoInput();
        input.setCnpj(cnpj);
        return input;
    }

    public static Empresa persistedEntity() {
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        empresa.setLegalName(LEGAL_NAME);
        empresa.setTradeName(TRADE_NAME);
        empresa.setCnpj(CNPJ);
        empresa.setInscricaoEstadual("123456789");
        empresa.setTaxRegime("SIMPLES_NACIONAL");
        empresa.setEmail("contato@postobom.com.br");
        empresa.setTelefone(PHONE);
        empresa.setAtivo(true);
        return empresa;
    }
}
