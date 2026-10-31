package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftClosingServiceTest {

    private static final UUID CLOSED_BY = UUID.randomUUID();

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private NozzleReadingRepository nozzleReadingRepository;

    @Spy
    private ShiftMapper shiftMapper = new ShiftMapper();

    @InjectMocks
    private ShiftClosingService shiftClosingService;

    @Test
    void shouldCloseOpenShift() {
        Shift shift = ShiftFixture.openShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(shiftRepository.saveAndFlush(any(Shift.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShiftResponseDTO response = shiftClosingService.closeShift(shift.getId(), ShiftFixture.closeRequest(CLOSED_BY));

        ArgumentCaptor<Shift> captor = ArgumentCaptor.forClass(Shift.class);
        verify(shiftRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
        assertThat(captor.getValue().getClosedAt()).isNotNull();
        assertThat(captor.getValue().getClosedBy()).isEqualTo(CLOSED_BY);
        assertThat(response.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
        assertThat(response.getClosedBy()).isEqualTo(CLOSED_BY);
    }

    @Test
    void shouldCloseWhenEveryNozzleHasClosingReading() {
        Shift shift = ShiftFixture.openShift();
        Nozzle nozzle = ShiftATestData.nozzle(UUID.randomUUID(), ShiftATestData.NOZZLE_METER);
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(validationSupport.findNozzlesByStationId(shift.getStationId())).thenReturn(List.of(nozzle));
        when(nozzleReadingRepository.existsByShiftIdAndNozzleIdAndReadingType(
                shift.getId(), nozzle.getId(), NozzleReadingTypeEnum.CLOSING)).thenReturn(true);
        when(shiftRepository.saveAndFlush(any(Shift.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShiftResponseDTO response = shiftClosingService.closeShift(shift.getId(), ShiftFixture.closeRequest(CLOSED_BY));

        assertThat(response.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
    }

    @Test
    void shouldRejectWhenNozzleHasNoClosingReading() {
        Shift shift = ShiftFixture.openShift();
        Nozzle nozzle = ShiftATestData.nozzle(UUID.randomUUID(), ShiftATestData.NOZZLE_METER);
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(validationSupport.findNozzlesByStationId(shift.getStationId())).thenReturn(List.of(nozzle));

        assertThatThrownBy(() -> shiftClosingService.closeShift(shift.getId(), ShiftFixture.closeRequest(CLOSED_BY)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(nozzle.getId().toString());
        verify(shiftRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldRejectNullRequest() {
        assertThatThrownBy(() -> shiftClosingService.closeShift(UUID.randomUUID(), null))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(validationSupport, shiftRepository);
    }

    @Test
    void shouldRejectRequestWithoutClosedBy() {
        assertThatThrownBy(() -> shiftClosingService.closeShift(UUID.randomUUID(), ShiftFixture.closeRequest(null)))
                .isInstanceOf(BusinessException.class);
        verifyNoInteractions(validationSupport, shiftRepository);
    }

    @Test
    void shouldPropagateNotFoundWhenShiftIsUnknown() {
        UUID shiftId = UUID.randomUUID();
        when(validationSupport.getRequiredShift(shiftId)).thenThrow(new NotFoundBusinessException("turno"));

        assertThatThrownBy(() -> shiftClosingService.closeShift(shiftId, ShiftFixture.closeRequest(CLOSED_BY)))
                .isInstanceOf(NotFoundBusinessException.class);
        verifyNoInteractions(shiftRepository);
    }

    @Test
    void shouldRejectAlreadyClosedShift() {
        Shift shift = ShiftFixture.closedShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);

        assertThatThrownBy(() -> shiftClosingService.closeShift(shift.getId(), ShiftFixture.closeRequest(CLOSED_BY)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(shift.getId().toString());
        verify(shiftRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldPropagateNotFoundWhenClosingUserIsUnknown() {
        Shift shift = ShiftFixture.openShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(validationSupport.getRequiredUser(CLOSED_BY, "closedBy")).thenThrow(new NotFoundBusinessException("usuario"));

        assertThatThrownBy(() -> shiftClosingService.closeShift(shift.getId(), ShiftFixture.closeRequest(CLOSED_BY)))
                .isInstanceOf(NotFoundBusinessException.class);
        verify(shiftRepository, never()).saveAndFlush(any());
    }
}
