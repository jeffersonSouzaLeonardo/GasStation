package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import com.br.manager.domain.operational.fixture.PaymentMethodFixture;
import com.br.manager.domain.operational.mapper.PaymentMethodMapper;
import com.br.manager.domain.operational.repository.PaymentMethodRepository;
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
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Spy
    private PaymentMethodMapper paymentMethodMapper = new PaymentMethodMapper();

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnPaymentMethod() {
            when(paymentMethodRepository.saveAndFlush(any(PaymentMethod.class))).thenAnswer(invocation -> {
                PaymentMethod paymentMethod = invocation.getArgument(0);
                paymentMethod.setId(UUID.randomUUID());
                return paymentMethod;
            });

            PaymentMethodResponseDTO response = paymentMethodService.create(PaymentMethodFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getKind()).isEqualTo(PaymentMethodFixture.KIND);
            assertThat(response.getSettlementDays()).isEqualTo(PaymentMethodFixture.SETTLEMENT_DAYS);
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            when(paymentMethodRepository.saveAndFlush(any(PaymentMethod.class)))
                    .thenThrow(OperationalBViolations.violationsOf(new PaymentMethod()));

            assertThatThrownBy(() -> paymentMethodService.create(PaymentMethodFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Payment method kind is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(paymentMethodRepository.saveAndFlush(any(PaymentMethod.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> paymentMethodService.create(PaymentMethodFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar forma de pagamento")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingPaymentMethod() {
            PaymentMethod existing = PaymentMethodFixture.persistedEntity();
            PaymentMethodInputDTO input = PaymentMethodFixture.otherInput();
            input.setId(existing.getId());
            when(paymentMethodRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(paymentMethodRepository.saveAndFlush(existing)).thenReturn(existing);

            PaymentMethodResponseDTO response = paymentMethodService.update(input);

            assertThat(response.getName()).isEqualTo(PaymentMethodFixture.OTHER_NAME);
            assertThat(response.getKind()).isEqualTo(PaymentMethodKindEnum.MONEY);
        }

        @Test
        void shouldThrowNotFoundWhenPaymentMethodDoesNotExist() {
            PaymentMethodInputDTO input = PaymentMethodFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> paymentMethodService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(paymentMethodRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            PaymentMethod existing = PaymentMethodFixture.persistedEntity();
            PaymentMethodInputDTO input = PaymentMethodFixture.validInput();
            input.setId(existing.getId());
            when(paymentMethodRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(paymentMethodRepository.saveAndFlush(existing))
                    .thenThrow(OperationalBViolations.violationsOf(new PaymentMethod()));

            assertThatThrownBy(() -> paymentMethodService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("Payment method name is required.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PaymentMethod existing = PaymentMethodFixture.persistedEntity();
            PaymentMethodInputDTO input = PaymentMethodFixture.validInput();
            input.setId(existing.getId());
            when(paymentMethodRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(paymentMethodRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> paymentMethodService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar forma de pagamento")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActivePaymentMethods() {
            when(paymentMethodRepository.findAllByActiveTrue()).thenReturn(List.of(PaymentMethodFixture.persistedEntity()));

            assertThat(paymentMethodService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByName() {
            when(paymentMethodRepository.findByNameContainingIgnoreCaseAndActiveTrue("visa"))
                    .thenReturn(List.of(PaymentMethodFixture.persistedEntity()));

            assertThat(paymentMethodService.findByDescription("visa"))
                    .extracting(PaymentMethodResponseDTO::getName)
                    .containsExactly(PaymentMethodFixture.NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(paymentMethodRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(paymentMethodService.findByDescription(" ")).isEmpty();
            verify(paymentMethodRepository, never()).findByNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            PaymentMethod existing = PaymentMethodFixture.persistedEntity();
            when(paymentMethodRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(paymentMethodService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> paymentMethodService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivatePaymentMethod() {
            PaymentMethod existing = PaymentMethodFixture.persistedEntity();
            when(paymentMethodRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            paymentMethodService.delete(existing.getId());

            ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
            verify(paymentMethodRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownPaymentMethod() {
            UUID id = UUID.randomUUID();
            when(paymentMethodRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentMethodService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(paymentMethodRepository, never()).saveAndFlush(any());
        }
    }
}
