package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.entity.Supplier;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Fornecedor.
 */
public final class SupplierFixture {

    public static final String LEGAL_NAME = "Distribuidora Petro Sul LTDA";
    public static final String OTHER_LEGAL_NAME = "Comercial Lubrificantes Norte SA";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CNPJ = "12345678000190";
    public static final String OTHER_MASKED_CNPJ = "98.765.432/0001-10";
    public static final String OTHER_CNPJ = "98765432000110";
    public static final String MASKED_STATE_REGISTRATION = "123.456.789";
    public static final String STATE_REGISTRATION = "123456789";
    public static final String EMAIL = "compras@petrosul.com.br";
    public static final String MASKED_PHONE = "(51) 3333-4444";
    public static final String PHONE = "5133334444";
    public static final String ADDRESS_STREET = "Avenida das Industrias";
    public static final String ADDRESS_NUMBER = "1500";
    public static final String ADDRESS_COMPLEMENT = "Galpao 3";
    public static final String ADDRESS_NEIGHBORHOOD = "Distrito Industrial";
    public static final String ADDRESS_CITY = "Porto Alegre";
    public static final String ADDRESS_STATE = "RS";
    public static final String MASKED_ZIP_CODE = "90200-000";
    public static final String ZIP_CODE = "90200000";

    private SupplierFixture() {
    }

    public static SupplierInputDTO validInput() {
        SupplierInputDTO input = new SupplierInputDTO();
        input.setLegalName(LEGAL_NAME);
        input.setCnpjCpf(MASKED_CNPJ);
        input.setStateRegistration(MASKED_STATE_REGISTRATION);
        input.setEmail(EMAIL);
        input.setPhone(MASKED_PHONE);
        input.setAddressStreet(ADDRESS_STREET);
        input.setAddressNumber(ADDRESS_NUMBER);
        input.setAddressComplement(ADDRESS_COMPLEMENT);
        input.setAddressNeighborhood(ADDRESS_NEIGHBORHOOD);
        input.setAddressCity(ADDRESS_CITY);
        input.setAddressState(ADDRESS_STATE);
        input.setAddressZipCode(MASKED_ZIP_CODE);
        input.setActive(true);
        return input;
    }

    public static SupplierInputDTO otherInput() {
        SupplierInputDTO input = validInput();
        input.setLegalName(OTHER_LEGAL_NAME);
        input.setCnpjCpf(OTHER_MASKED_CNPJ);
        return input;
    }

    public static Supplier persistedEntity() {
        Supplier supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        supplier.setLegalName(LEGAL_NAME);
        supplier.setCnpjCpf(CNPJ);
        supplier.setStateRegistration(STATE_REGISTRATION);
        supplier.setEmail(EMAIL);
        supplier.setPhone(PHONE);
        supplier.setAddressStreet(ADDRESS_STREET);
        supplier.setAddressNumber(ADDRESS_NUMBER);
        supplier.setAddressComplement(ADDRESS_COMPLEMENT);
        supplier.setAddressNeighborhood(ADDRESS_NEIGHBORHOOD);
        supplier.setAddressCity(ADDRESS_CITY);
        supplier.setAddressState(ADDRESS_STATE);
        supplier.setAddressZipCode(ZIP_CODE);
        supplier.setActive(true);
        return supplier;
    }
}
