package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.common.StringUtils;
import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.entity.Company;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyMapper {

    public Company companyInputDTOToCompany(CompanyInputDTO inputDTO) {
        Company company = new Company();
        company.setId(inputDTO.getId());
        company.setLegalName(inputDTO.getLegalName());
        company.setTradeName(inputDTO.getTradeName());
        company.setCnpj(StringUtils.removeMask(inputDTO.getCnpj()));
        company.setStateRegistration(StringUtils.removeMask(inputDTO.getStateRegistration()));
        company.setTaxRegime(inputDTO.getTaxRegime());
        company.setEmail(inputDTO.getEmail());
        company.setPhone(StringUtils.removeMask(inputDTO.getPhone()));
        company.setActive(inputDTO.getActive());
        return company;
    }

    public CompanyResponseDTO companyToCompanyResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setLegalName(company.getLegalName());
        dto.setTradeName(company.getTradeName());
        dto.setCnpj(company.getCnpj());
        dto.setStateRegistration(company.getStateRegistration());
        dto.setTaxRegime(company.getTaxRegime());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setActive(company.getActive());
        return dto;
    }

    public List<CompanyResponseDTO> listCompanyToListCompanyResponseDTO(List<Company> companies) {
        return companies.stream().map(this::companyToCompanyResponseDTO).toList();
    }

    public void updateCompanyFromDto(CompanyInputDTO dto, Company entity) {
        entity.setLegalName(dto.getLegalName());
        entity.setTradeName(dto.getTradeName());
        entity.setCnpj(StringUtils.removeMask(dto.getCnpj()));
        entity.setStateRegistration(StringUtils.removeMask(dto.getStateRegistration()));
        entity.setTaxRegime(dto.getTaxRegime());
        entity.setEmail(dto.getEmail());
        entity.setPhone(StringUtils.removeMask(dto.getPhone()));
        entity.setActive(dto.getActive());
    }
}
