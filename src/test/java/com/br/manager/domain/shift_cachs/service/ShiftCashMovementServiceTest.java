package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.CashMovementFixture;
import com.br.manager.domain.shift_cachs.mapper.CashMovementMapper;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftCashMovementServiceTest {

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private CashMovementRepository cashMovementRepository;

    @Spy
    private CashMovementMapper cashMovementMapper = new CashMovementMapper();

    @InjectMocks
    private ShiftCashMovementService service;

    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);
    private final CashMovementInputDTO input = CashMovementFixture.validInput(UUID.randomUUID());

    @Test
    void shouldRegisterCashMovementLinkedToShift() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(cashMovementRepository.saveAndFlush(any(CashMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashMovementResponseDTO response = service.registerCashMovement(shift.getId(), input);

        ArgumentCaptor<CashMovement> captor = ArgumentCaptor.forClass(CashMovement.class);
        verify(cashMovementRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getShift()).isSameAs(shift);
        assertThat(captor.getValue().getAmount()).isEqualTo(CashMovementFixture.AMOUNT);
        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response.getMovementType()).isEqualTo(CashMovementFixture.MOVEMENT_TYPE);
        assertThat(response.getCreatedBy()).isEqualTo(input.getCreatedBy());
    }

    @Test
    void shouldValidateOpenShiftAndCreator() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(cashMovementRepository.saveAndFlush(any(CashMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.registerCashMovement(shift.getId(), input);

        verify(validationSupport).ensureShiftIsOpen(shift);
        verify(validationSupport).getRequiredUser(input.getCreatedBy(), "createdBy");
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsNull() {
        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), null))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(validationSupport, cashMovementRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenShiftDoesNotExist() {
        when(validationSupport.getRequiredShift(shift.getId())).thenThrow(new NotFoundBusinessException("turno"));

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(NotFoundBusinessException.class);
        verifyNoInteractions(cashMovementRepository);
    }

    @Test
    void shouldPropagateBusinessExceptionWhenShiftIsClosed() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        doThrow(new BusinessException("fechado")).when(validationSupport).ensureShiftIsOpen(shift);

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(cashMovementRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenMovementTypeIsNull() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        input.setMovementType(null);

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("tipo de movimentação");
        verifyNoInteractions(cashMovementRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenAmountIsNull() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        input.setAmount(null);

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("valor");
        verifyNoInteractions(cashMovementRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenAmountIsNegative() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        input.setAmount(CashMovementFixture.NEGATIVE_AMOUNT);

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("valor");
        verifyNoInteractions(cashMovementRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenCreatorDoesNotExist() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(validationSupport.getRequiredUser(input.getCreatedBy(), "createdBy")).thenThrow(new NotFoundBusinessException("usuario"));

        assertThatThrownBy(() -> service.registerCashMovement(shift.getId(), input))
                .isInstanceOf(NotFoundBusinessException.class);
        verify(cashMovementRepository, never()).saveAndFlush(any());
    }
}
