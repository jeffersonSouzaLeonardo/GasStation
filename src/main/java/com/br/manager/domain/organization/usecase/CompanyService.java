package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.mapper.CompanyMapper;
import com.br.manager.domain.organization.repository.CompanyRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    public CompanyResponseDTO create(CompanyInputDTO inputDTO) {
        try {
            Company company = companyMapper.companyInputDTOToCompany(inputDTO);
            if (company.getId() == null) {
                company.setId(UUID.randomUUID());
            }
            return companyMapper.companyToCompanyResponseDTO(companyRepository.saveAndFlush(company));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String companyName = inputDTO != null && StringUtils.hasText(inputDTO.getLegalName()) ? inputDTO.getLegalName() : "";
            throw new BusinessException("Error while saving company " + companyName, e);
        }
    }

    public CompanyResponseDTO update(CompanyInputDTO inputDTO) {
        try {
            Company company = companyRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (company == null) {
                throw new NotFoundBusinessException(String.format("Company with ID %s not found", inputDTO.getId()));
            }
            companyMapper.updateCompanyFromDto(inputDTO, company);
            return companyMapper.companyToCompanyResponseDTO(companyRepository.saveAndFlush(company));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String companyName = inputDTO != null && StringUtils.hasText(inputDTO.getLegalName()) ? inputDTO.getLegalName() : "";
            throw new BusinessException("Error while saving company " + companyName, e);
        }
    }

    public List<CompanyResponseDTO> findAll() {
        return companyMapper.listCompanyToListCompanyResponseDTO(companyRepository.findAllByActiveTrue());
    }

    public List<CompanyResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return companyMapper.listCompanyToListCompanyResponseDTO(
                companyRepository.findByLegalNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public List<CompanyResponseDTO> findByName(String name) {
        return findByDescription(name);
    }

    public CompanyResponseDTO find(UUID id) {
        Company company = companyRepository.findByIdAndActiveTrue(id);
        if (company == null) {
            throw new NotFoundBusinessException(String.format("Company with ID %s not found", id));
        }
        return companyMapper.companyToCompanyResponseDTO(company);
    }

    public void delete(UUID id) {
        try {
            Company company = companyRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Company with ID %s not found", id)));
            company.setActive(false);
            companyRepository.saveAndFlush(company);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting company", e);
        }
    }

    public Company getCompanyEntityById(UUID id) {
        Company company = companyRepository.findByIdAndActiveTrue(id);
        if (company == null) {
            throw new NotFoundBusinessException(String.format("Company with ID %s not found", id));
        }
        return company;
    }
}
