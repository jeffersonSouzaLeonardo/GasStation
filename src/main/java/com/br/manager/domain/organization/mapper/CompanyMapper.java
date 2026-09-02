package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company companyInputDTOToCompany(CompanyInputDTO inputDTO);

    CompanyResponseDTO companyToCompanyResponseDTO(Company company);

    List<CompanyResponseDTO> listCompanyToListCompanyResponseDTO(List<Company> companies);

    void updateCompanyFromDto(CompanyInputDTO dto, @MappingTarget Company entity);
}
