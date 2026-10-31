package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.fixture.CustomerVehicleFixture;
import com.br.manager.domain.operational.mapper.CustomerVehicleMapper;
import com.br.manager.domain.operational.repository.CustomerVehicleRepository;
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
class CustomerVehicleServiceTest {

    @Mock
    private CustomerVehicleRepository customerVehicleRepository;

    @Spy
    private CustomerVehicleMapper customerVehicleMapper = new CustomerVehicleMapper();

    @InjectMocks
    private CustomerVehicleService customerVehicleService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnVehicle() {
            UUID customerId = UUID.randomUUID();
            when(customerVehicleRepository.saveAndFlush(any(CustomerVehicle.class))).thenAnswer(invocation -> {
                CustomerVehicle vehicle = invocation.getArgument(0);
                vehicle.setId(UUID.randomUUID());
                return vehicle;
            });

            CustomerVehicleResponseDTO response = customerVehicleService.create(CustomerVehicleFixture.validInput(customerId));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCustomerId()).isEqualTo(customerId);
            assertThat(response.getModel()).isEqualTo(CustomerVehicleFixture.MODEL);
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            when(customerVehicleRepository.saveAndFlush(any(CustomerVehicle.class)))
                    .thenThrow(OperationalBViolations.violationsOf(new CustomerVehicle()));

            assertThatThrownBy(() -> customerVehicleService.create(CustomerVehicleFixture.validInput(null)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Customer ID is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(customerVehicleRepository.saveAndFlush(any(CustomerVehicle.class)))
                    .thenThrow(new DataIntegrityViolationException("fk_vehicles_customer"));

            assertThatThrownBy(() -> customerVehicleService.create(CustomerVehicleFixture.validInput(UUID.randomUUID())))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar veículo do cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingVehicle() {
            CustomerVehicle existing = CustomerVehicleFixture.persistedEntity();
            CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(existing.getCustomerId());
            input.setId(existing.getId());
            input.setBrand("Scania");
            when(customerVehicleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerVehicleRepository.saveAndFlush(existing)).thenReturn(existing);

            CustomerVehicleResponseDTO response = customerVehicleService.update(input);

            assertThat(response.getBrand()).isEqualTo("Scania");
        }

        @Test
        void shouldThrowNotFoundWhenVehicleDoesNotExist() {
            CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> customerVehicleService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(customerVehicleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            CustomerVehicle existing = CustomerVehicleFixture.persistedEntity();
            CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(existing.getCustomerId());
            input.setId(existing.getId());
            when(customerVehicleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerVehicleRepository.saveAndFlush(existing))
                    .thenThrow(OperationalBViolations.violationsOf(new CustomerVehicle()));

            assertThatThrownBy(() -> customerVehicleService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("Vehicle plate is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            CustomerVehicle existing = CustomerVehicleFixture.persistedEntity();
            CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(existing.getCustomerId());
            input.setId(existing.getId());
            when(customerVehicleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(customerVehicleRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> customerVehicleService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar veículo do cliente")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveVehicles() {
            when(customerVehicleRepository.findAllByActiveTrue()).thenReturn(List.of(CustomerVehicleFixture.persistedEntity()));

            assertThat(customerVehicleService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByPlate() {
            when(customerVehicleRepository.findByPlateContainingIgnoreCaseAndActiveTrue("abc"))
                    .thenReturn(List.of(CustomerVehicleFixture.persistedEntity()));

            assertThat(customerVehicleService.findByDescription("abc"))
                    .extracting(CustomerVehicleResponseDTO::getPlate)
                    .containsExactly(CustomerVehicleFixture.PLATE);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(customerVehicleRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(customerVehicleService.findByDescription(null)).isEmpty();
            verify(customerVehicleRepository, never()).findByPlateContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            CustomerVehicle existing = CustomerVehicleFixture.persistedEntity();
            when(customerVehicleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(customerVehicleService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> customerVehicleService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateVehicle() {
            CustomerVehicle existing = CustomerVehicleFixture.persistedEntity();
            when(customerVehicleRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            customerVehicleService.delete(existing.getId());

            ArgumentCaptor<CustomerVehicle> captor = ArgumentCaptor.forClass(CustomerVehicle.class);
            verify(customerVehicleRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownVehicle() {
            UUID id = UUID.randomUUID();
            when(customerVehicleRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerVehicleService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(customerVehicleRepository, never()).saveAndFlush(any());
        }
    }
}
