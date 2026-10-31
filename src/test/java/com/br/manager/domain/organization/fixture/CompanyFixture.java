package com.br.manager.domain.organization.fixture;

import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.entity.Company;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Empresa.
 */
public final class CompanyFixture {

    public static final String LEGAL_NAME = "Posto Bom Combustivel LTDA";
    public static final String TRADE_NAME = "Posto Bom";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CNPJ = "12345678000190";
    public static final String MASKED_PHONE = "(11) 98765-4321";
    public static final String PHONE = "11987654321";

    private CompanyFixture() {
    }

    public static CompanyInputDTO validInput() {
        CompanyInputDTO input = new CompanyInputDTO();
        input.setLegalName(LEGAL_NAME);
        input.setTradeName(TRADE_NAME);
        input.setCnpj(MASKED_CNPJ);
        input.setStateRegistration("123.456.789");
        input.setTaxRegime("SIMPLES_NACIONAL");
        input.setEmail("contato@postobom.com.br");
        input.setPhone(MASKED_PHONE);
        input.setActive(true);
        return input;
    }

    public static CompanyInputDTO validInput(String cnpj) {
        CompanyInputDTO input = validInput();
        input.setCnpj(cnpj);
        return input;
    }

    public static Company persistedEntity() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setLegalName(LEGAL_NAME);
        company.setTradeName(TRADE_NAME);
        company.setCnpj(CNPJ);
        company.setStateRegistration("123456789");
        company.setTaxRegime("SIMPLES_NACIONAL");
        company.setEmail("contato@postobom.com.br");
        company.setPhone(PHONE);
        company.setActive(true);
        return company;
    }
}
