package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.NozzleReadingFixture;
import com.br.manager.domain.shift_cachs.mapper.NozzleReadingMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
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
class ShiftNozzleReadingServiceTest {

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private NozzleReadingRepository nozzleReadingRepository;

    @Spy
    private NozzleReadingMapper nozzleReadingMapper = new NozzleReadingMapper();

    @InjectMocks
    private ShiftNozzleReadingService service;

    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);
    private final UUID nozzleId = UUID.randomUUID();

    private NozzleReadingInputDTO input(NozzleReadingTypeEnum type) {
        return NozzleReadingFixture.validInput(nozzleId, UUID.randomUUID(), type);
    }

    private void givenOpenShift() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
    }

    private void givenReadingAlreadyRegistered(NozzleReadingTypeEnum type) {
        when(nozzleReadingRepository.existsByShiftIdAndNozzleIdAndReadingType(shift.getId(), nozzleId, type)).thenReturn(true);
    }

    private void returnSavedReading() {
        when(nozzleReadingRepository.saveAndFlush(any(NozzleReading.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldRegisterClosingReadingLinkedToShift() {
        givenOpenShift();
        returnSavedReading();

        NozzleReadingResponseDTO response = service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.CLOSING));

        ArgumentCaptor<NozzleReading> captor = ArgumentCaptor.forClass(NozzleReading.class);
        verify(nozzleReadingRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getShift()).isSameAs(shift);
        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response.getNozzleId()).isEqualTo(nozzleId);
        assertThat(response.getReadingType()).isEqualTo(NozzleReadingTypeEnum.CLOSING);
        assertThat(response.getMeterReading()).isEqualTo(NozzleReadingFixture.METER_READING);
    }

    @Test
    void shouldValidateShiftIsOpenAndNozzleBelongsToStation() {
        givenOpenShift();
        returnSavedReading();

        service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.OPENING));

        verify(validationSupport).ensureShiftIsOpen(shift);
        verify(validationSupport).validateNozzleBelongsToShiftStation(shift.getStationId(), nozzleId);
    }

    @Test
    void shouldAllowRepeatedTestReadings() {
        givenOpenShift();
        returnSavedReading();

        service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.TEST));

        verify(nozzleReadingRepository, never()).existsByShiftIdAndNozzleIdAndReadingType(any(), any(), any());
        verify(nozzleReadingRepository).saveAndFlush(any(NozzleReading.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsNull() {
        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), null))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(validationSupport, nozzleReadingRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenShiftDoesNotExist() {
        when(validationSupport.getRequiredShift(shift.getId())).thenThrow(new NotFoundBusinessException("turno"));

        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.CLOSING)))
                .isInstanceOf(NotFoundBusinessException.class);
        verifyNoInteractions(nozzleReadingRepository);
    }

    @Test
    void shouldPropagateBusinessExceptionWhenShiftIsClosed() {
        givenOpenShift();
        doThrow(new BusinessException("fechado")).when(validationSupport).ensureShiftIsOpen(shift);

        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.CLOSING)))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(nozzleReadingRepository);
    }

    @Test
    void shouldPropagateBusinessExceptionWhenNozzleDoesNotBelongToStation() {
        givenOpenShift();
        doThrow(new BusinessException("bico")).when(validationSupport)
                .validateNozzleBelongsToShiftStation(shift.getStationId(), nozzleId);

        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.CLOSING)))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(nozzleReadingRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenOpeningReadingAlreadyExists() {
        givenOpenShift();
        givenReadingAlreadyRegistered(NozzleReadingTypeEnum.OPENING);

        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.OPENING)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("abertura")
                .hasMessageContaining(nozzleId.toString());
        verify(nozzleReadingRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenClosingReadingAlreadyExists() {
        givenOpenShift();
        givenReadingAlreadyRegistered(NozzleReadingTypeEnum.CLOSING);

        assertThatThrownBy(() -> service.registerNozzleReading(shift.getId(), input(NozzleReadingTypeEnum.CLOSING)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("fechamento")
                .hasMessageContaining(nozzleId.toString());
        verify(nozzleReadingRepository, never()).saveAndFlush(any());
    }
}
