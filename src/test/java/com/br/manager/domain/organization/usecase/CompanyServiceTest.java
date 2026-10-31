package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.entity.Company;
import com.br.manager.domain.organization.fixture.CompanyFixture;
import com.br.manager.domain.organization.mapper.CompanyMapper;
import com.br.manager.domain.organization.repository.CompanyRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Spy
    private CompanyMapper companyMapper = new CompanyMapper();

    @InjectMocks
    private CompanyService companyService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnCompany() {
            when(companyRepository.saveAndFlush(any(Company.class))).thenAnswer(invocation -> {
                Company company = invocation.getArgument(0);
                company.setId(UUID.randomUUID());
                return company;
            });

            CompanyResponseDTO response = companyService.create(CompanyFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpj()).isEqualTo(CompanyFixture.CNPJ);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(companyRepository.saveAndFlush(any(Company.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate cnpj"));

            assertThatThrownBy(() -> companyService.create(CompanyFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CompanyFixture.LEGAL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingCompany() {
            Company existing = CompanyFixture.persistedEntity();
            CompanyInputDTO input = CompanyFixture.validInput();
            input.setId(existing.getId());
            input.setLegalName("Razao Atualizada");
            when(companyRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(companyRepository.saveAndFlush(existing)).thenReturn(existing);

            CompanyResponseDTO response = companyService.update(input);

            assertThat(response.getLegalName()).isEqualTo("Razao Atualizada");
        }

        @Test
        void shouldThrowNotFoundWhenCompanyDoesNotExist() {
            CompanyInputDTO input = CompanyFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> companyService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(companyRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveCompanies() {
            when(companyRepository.findAllByActiveTrue()).thenReturn(List.of(CompanyFixture.persistedEntity()));

            assertThat(companyService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByLegalName() {
            when(companyRepository.findByLegalNameContainingIgnoreCaseAndActiveTrue("bom"))
                    .thenReturn(List.of(CompanyFixture.persistedEntity()));

            assertThat(companyService.findByName("bom")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(companyRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(companyService.findByDescription(" ")).isEmpty();
            verify(companyRepository, never()).findByLegalNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Company existing = CompanyFixture.persistedEntity();
            when(companyRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(companyService.find(existing.getId()).getId()).isEqualTo(existing.getId());
            assertThat(companyService.getCompanyEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> companyService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> companyService.getCompanyEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateCompany() {
            Company existing = CompanyFixture.persistedEntity();
            when(companyRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            companyService.delete(existing.getId());

            ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
            verify(companyRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownCompany() {
            UUID id = UUID.randomUUID();
            when(companyRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }
}
