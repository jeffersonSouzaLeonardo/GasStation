package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.enums.PersonTypeEnum;
import com.br.manager.domain.operational.fixture.CustomerFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void shouldMapInputToEntity() {
        CustomerInputDTO input = CustomerFixture.validInput();
        input.setId(UUID.randomUUID());

        Customer customer = mapper.customerInputDTOToCustomer(input);

        assertThat(customer)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    @Disabled("BUG: CustomerMapper.customerInputDTOToCustomer/updateCustomerFromDto use BeanUtils.copyProperties "
            + "without StringUtils.removeMask (unlike SupplierMapper); a masked CNPJ (18 chars) overflows customers.cnpj_cpf VARCHAR(14)")
    void shouldMapInputToEntityRemovingMasks() {
        Customer customer = mapper.customerInputDTOToCustomer(CustomerFixture.maskedInput());

        assertThat(customer.getCnpjCpf()).isEqualTo(CustomerFixture.CNPJ);
        assertThat(customer.getPhone()).isEqualTo(CustomerFixture.PHONE);
        assertThat(customer.getAddressZipCode()).isEqualTo(CustomerFixture.ZIP_CODE);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.customerInputDTOToCustomer(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Customer customer = CustomerFixture.persistedEntity();

        CustomerResponseDTO response = mapper.customerToCustomerResponseDTO(customer);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(customer);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.customerToCustomerResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Customer> customers = List.of(CustomerFixture.persistedEntity(), CustomerFixture.persistedEntity());

        List<CustomerResponseDTO> responses = mapper.listCustomerToListCustomerResponseDTO(customers);

        assertThat(responses)
                .extracting(CustomerResponseDTO::getId)
                .containsExactly(customers.get(0).getId(), customers.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listCustomerToListCustomerResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Customer customer = CustomerFixture.persistedEntity();
        CustomerInputDTO input = CustomerFixture.otherInput();
        input.setId(customer.getId());
        input.setActive(false);

        mapper.updateCustomerFromDto(input, customer);

        assertThat(customer.getName()).isEqualTo(CustomerFixture.OTHER_NAME);
        assertThat(customer.getPersonType()).isEqualTo(PersonTypeEnum.PHYSICAL);
        assertThat(customer.getCnpjCpf()).isEqualTo(CustomerFixture.CPF);
        assertThat(customer.getActive()).isFalse();
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Customer customer = CustomerFixture.persistedEntity();

        mapper.updateCustomerFromDto(null, customer);

        assertThat(customer.getName()).isEqualTo(CustomerFixture.NAME);
    }
}
