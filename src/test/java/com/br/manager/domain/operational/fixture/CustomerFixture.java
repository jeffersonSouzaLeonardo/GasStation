package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.enums.PersonTypeEnum;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Cliente.
 */
public final class CustomerFixture {

    public static final String NAME = "Joao da Silva Transportes";
    public static final String OTHER_NAME = "Maria Oliveira";
    public static final PersonTypeEnum PERSON_TYPE = PersonTypeEnum.JURIDICAL;
    public static final String CNPJ = "12345678000190";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CPF = "12345678909";
    public static final String STATE_REGISTRATION = "123456789";
    public static final String EMAIL = "financeiro@silvatransportes.com.br";
    public static final String PHONE = "11987654321";
    public static final String MASKED_PHONE = "(11) 98765-4321";
    public static final String ADDRESS_STREET = "Rua das Flores";
    public static final String ADDRESS_NUMBER = "100";
    public static final String ADDRESS_COMPLEMENT = "Sala 2";
    public static final String ADDRESS_NEIGHBORHOOD = "Centro";
    public static final String ADDRESS_CITY = "Sao Paulo";
    public static final String ADDRESS_STATE = "SP";
    public static final String ZIP_CODE = "01310100";
    public static final String MASKED_ZIP_CODE = "01310-100";
    public static final BigDecimal CREDIT_LIMIT = new BigDecimal("1500.00");

    private CustomerFixture() {
    }

    public static CustomerInputDTO validInput() {
        CustomerInputDTO input = new CustomerInputDTO();
        input.setName(NAME);
        input.setPersonType(PERSON_TYPE);
        input.setCnpjCpf(CNPJ);
        input.setStateRegistration(STATE_REGISTRATION);
        input.setEmail(EMAIL);
        input.setPhone(PHONE);
        input.setAddressStreet(ADDRESS_STREET);
        input.setAddressNumber(ADDRESS_NUMBER);
        input.setAddressComplement(ADDRESS_COMPLEMENT);
        input.setAddressNeighborhood(ADDRESS_NEIGHBORHOOD);
        input.setAddressCity(ADDRESS_CITY);
        input.setAddressState(ADDRESS_STATE);
        input.setAddressZipCode(ZIP_CODE);
        input.setCreditLimit(CREDIT_LIMIT);
        input.setActive(true);
        return input;
    }

    public static CustomerInputDTO maskedInput() {
        CustomerInputDTO input = validInput();
        input.setCnpjCpf(MASKED_CNPJ);
        input.setPhone(MASKED_PHONE);
        input.setAddressZipCode(MASKED_ZIP_CODE);
        return input;
    }

    public static CustomerInputDTO otherInput() {
        CustomerInputDTO input = validInput();
        input.setName(OTHER_NAME);
        input.setPersonType(PersonTypeEnum.PHYSICAL);
        input.setCnpjCpf(CPF);
        return input;
    }

    public static Customer persistedEntity() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName(NAME);
        customer.setPersonType(PERSON_TYPE);
        customer.setCnpjCpf(CNPJ);
        customer.setStateRegistration(STATE_REGISTRATION);
        customer.setEmail(EMAIL);
        customer.setPhone(PHONE);
        customer.setAddressStreet(ADDRESS_STREET);
        customer.setAddressNumber(ADDRESS_NUMBER);
        customer.setAddressComplement(ADDRESS_COMPLEMENT);
        customer.setAddressNeighborhood(ADDRESS_NEIGHBORHOOD);
        customer.setAddressCity(ADDRESS_CITY);
        customer.setAddressState(ADDRESS_STATE);
        customer.setAddressZipCode(ZIP_CODE);
        customer.setCreditLimit(CREDIT_LIMIT);
        customer.setActive(true);
        return customer;
    }
}
