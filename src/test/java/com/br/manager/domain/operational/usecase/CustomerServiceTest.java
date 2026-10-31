package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.fixture.CustomerFixture;
import com.br.manager.domain.operational.mapper.CustomerMapper;
import com.br.manager.domain.operational.repository.CustomerRepository;
import com.br.manager.domain.operational.support.OperationalBViolations;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapper();

    @InjectMocks
    private CustomerService customerService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnCustomer() {
            when(customerRepository.saveAndFlush(any(Customer.class))).thenAnswer(invocation -> {
                Customer customer = invocation.getArgument(0);
                customer.setId(UUID.randomUUID());
                return customer;
            });

            CustomerResponseDTO response = customerService.create(CustomerFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getName()).isEqualTo(CustomerFixture.NAME);
            assertThat(response.getCreditLimit()).isEqualByComparingTo(CustomerFixture.CREDIT_LIMIT);
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            when(customerRepository.saveAndFlush(any(Customer.class)))
                    .thenThrow(OperationalBViolations.violationsOf(new Customer()));

            assertThatThrownBy(() -> customerService.create(CustomerFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Customer name is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(customerRepository.saveAndFlush(any(Customer.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> customerService.create(CustomerFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingCustomer() {
            Customer existing = CustomerFixture.persistedEntity();
            CustomerInputDTO input = CustomerFixture.otherInput();
            input.setId(existing.getId());
            when(customerRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerRepository.saveAndFlush(existing)).thenReturn(existing);

            CustomerResponseDTO response = customerService.update(input);

            assertThat(response.getName()).isEqualTo(CustomerFixture.OTHER_NAME);
            assertThat(response.getCnpjCpf()).isEqualTo(CustomerFixture.CPF);
        }

        @Test
        void shouldThrowNotFoundWhenCustomerDoesNotExist() {
            CustomerInputDTO input = CustomerFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> customerService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(customerRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            Customer existing = CustomerFixture.persistedEntity();
            CustomerInputDTO input = CustomerFixture.validInput();
            input.setId(existing.getId());
            when(customerRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerRepository.saveAndFlush(existing)).thenThrow(OperationalBViolations.violationsOf(new Customer()));

            assertThatThrownBy(() -> customerService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("Person type is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Customer existing = CustomerFixture.persistedEntity();
            CustomerInputDTO input = CustomerFixture.validInput();
            input.setId(existing.getId());
            when(customerRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> customerService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveCustomers() {
            when(customerRepository.findAllByActiveTrue()).thenReturn(List.of(CustomerFixture.persistedEntity()));

            assertThat(customerService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByName() {
            when(customerRepository.findByNameContainingIgnoreCaseAndActiveTrue("silva"))
                    .thenReturn(List.of(CustomerFixture.persistedEntity()));

            assertThat(customerService.findByDescription("silva"))
                    .extracting(CustomerResponseDTO::getName)
                    .containsExactly(CustomerFixture.NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(customerRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(customerService.findByDescription("")).isEmpty();
            verify(customerRepository, never()).findByNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Customer existing = CustomerFixture.persistedEntity();
            when(customerRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(customerService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> customerService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateCustomer() {
            Customer existing = CustomerFixture.persistedEntity();
            when(customerRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            customerService.delete(existing.getId());

            ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownCustomer() {
            UUID id = UUID.randomUUID();
            when(customerRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(customerRepository, never()).saveAndFlush(any());
        }
    }
}
