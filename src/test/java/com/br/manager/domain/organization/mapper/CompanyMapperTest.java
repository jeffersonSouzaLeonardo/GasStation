package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.fixture.CompanyFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest {

    private final CompanyMapper mapper = new CompanyMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        CompanyInputDTO input = CompanyFixture.validInput();
        input.setId(UUID.randomUUID());

        Company company = mapper.companyInputDTOToCompany(input);

        assertThat(company.getId()).isEqualTo(input.getId());
        assertThat(company.getLegalName()).isEqualTo(CompanyFixture.LEGAL_NAME);
        assertThat(company.getTradeName()).isEqualTo(CompanyFixture.TRADE_NAME);
        assertThat(company.getCnpj()).isEqualTo(CompanyFixture.CNPJ);
        assertThat(company.getStateRegistration()).isEqualTo("123456789");
        assertThat(company.getPhone()).isEqualTo(CompanyFixture.PHONE);
        assertThat(company.getTaxRegime()).isEqualTo(input.getTaxRegime());
        assertThat(company.getEmail()).isEqualTo(input.getEmail());
        assertThat(company.getActive()).isTrue();
    }

    @Test
    void shouldMapEntityToResponse() {
        Company company = CompanyFixture.persistedEntity();

        CompanyResponseDTO response = mapper.companyToCompanyResponseDTO(company);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(company);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Company> companies = List.of(CompanyFixture.persistedEntity(), CompanyFixture.persistedEntity());

        List<CompanyResponseDTO> responses = mapper.listCompanyToListCompanyResponseDTO(companies);

        assertThat(responses)
                .extracting(CompanyResponseDTO::getId)
                .containsExactly(companies.get(0).getId(), companies.get(1).getId());
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        Company company = CompanyFixture.persistedEntity();
        UUID originalId = company.getId();
        CompanyInputDTO input = CompanyFixture.validInput("98.765.432/0001-10");
        input.setLegalName("Nova Razao");
        input.setActive(false);

        mapper.updateCompanyFromDto(input, company);

        assertThat(company.getId()).isEqualTo(originalId);
        assertThat(company.getLegalName()).isEqualTo("Nova Razao");
        assertThat(company.getCnpj()).isEqualTo("98765432000110");
        assertThat(company.getActive()).isFalse();
    }
}
