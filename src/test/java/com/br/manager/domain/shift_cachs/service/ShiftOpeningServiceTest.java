package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftAllocationFixture;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.mapper.ShiftAllocationMapper;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftAllocationRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftOpeningServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftAllocationRepository allocationRepository;

    @Mock
    private NozzleReadingRepository nozzleReadingRepository;

    @Mock
    private ShiftValidationSupport validationSupport;

    @Spy
    private ShiftMapper shiftMapper = new ShiftMapper();

    @Spy
    private ShiftAllocationMapper shiftAllocationMapper = new ShiftAllocationMapper();

    @InjectMocks
    private ShiftOpeningService shiftOpeningService;

    private void givenShiftIsSaved() {
        when(shiftRepository.saveAndFlush(any(Shift.class))).thenAnswer(invocation -> {
            Shift shift = invocation.getArgument(0);
            shift.setId(UUID.randomUUID());
            return shift;
        });
    }

    @Nested
    class OpenShift {

        @Test
        void shouldPersistOpenShiftAndReturnResponse() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            givenShiftIsSaved();

            ShiftResponseDTO response = shiftOpeningService.openShift(request);

            ArgumentCaptor<Shift> captor = ArgumentCaptor.forClass(Shift.class);
            verify(shiftRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
            assertThat(captor.getValue().getOpenedAt()).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
            assertThat(response.getStationId()).isEqualTo(request.getStationId());
            assertThat(response.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
        }

        @Test
        void shouldValidateStationCashierAndOpener() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            givenShiftIsSaved();

            shiftOpeningService.openShift(request);

            verify(validationSupport).getRequiredStation(request.getStationId());
            verify(validationSupport).getRequiredUser(request.getCashierUserId(), "cashierUserId");
            verify(validationSupport).getRequiredUser(request.getOpenedBy(), "openedBy");
        }

        @Test
        void shouldRejectNullRequest() {
            assertThatThrownBy(() -> shiftOpeningService.openShift(null))
                    .isInstanceOf(BusinessException.class);
            verifyNoInteractions(shiftRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenStationIsUnknown() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            doThrow(new NotFoundBusinessException("posto")).when(validationSupport).getRequiredStation(request.getStationId());

            assertThatThrownBy(() -> shiftOpeningService.openShift(request))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(shiftRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectWhenStationAlreadyHasOpenShift() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            when(shiftRepository.existsByStationIdAndStatus(request.getStationId(), ShiftStatusEnum.OPEN)).thenReturn(true);

            assertThatThrownBy(() -> shiftOpeningService.openShift(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(request.getStationId().toString());
            verify(shiftRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectWhenCashierAlreadyHasOpenShiftInStation() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            when(shiftRepository.existsByStationIdAndCashierUserIdAndStatus(
                    request.getStationId(), request.getCashierUserId(), ShiftStatusEnum.OPEN)).thenReturn(true);

            assertThatThrownBy(() -> shiftOpeningService.openShift(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(request.getCashierUserId().toString());
            verify(shiftRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Allocations {

        @Test
        void shouldPersistEachAllocationLinkedToSavedShift() {
            ShiftAllocationInputDTO allocationInput = ShiftAllocationFixture.validInput();
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            request.setAllocations(List.of(allocationInput));
            givenShiftIsSaved();

            ShiftResponseDTO response = shiftOpeningService.openShift(request);

            ArgumentCaptor<ShiftAllocation> captor = ArgumentCaptor.forClass(ShiftAllocation.class);
            verify(allocationRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getShift().getId()).isEqualTo(response.getId());
            assertThat(captor.getValue().getUserId()).isEqualTo(allocationInput.getUserId());
            assertThat(captor.getValue().getRole()).isEqualTo(ShiftAllocationFixture.ROLE);
            verify(validationSupport).getRequiredUser(allocationInput.getUserId(), "allocationUserId");
        }

        @Test
        void shouldSkipNullAllocationEntries() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            request.setAllocations(Arrays.asList(null, ShiftAllocationFixture.validInput()));
            givenShiftIsSaved();

            shiftOpeningService.openShift(request);

            verify(allocationRepository, times(1)).saveAndFlush(any(ShiftAllocation.class));
        }

        @Test
        void shouldNotPersistAllocationsWhenListIsNull() {
            givenShiftIsSaved();

            shiftOpeningService.openShift(ShiftFixture.openRequest());

            verifyNoInteractions(allocationRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenAllocationUserIsUnknown() {
            ShiftAllocationInputDTO allocationInput = ShiftAllocationFixture.validInput();
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            request.setAllocations(List.of(allocationInput));
            givenShiftIsSaved();
            lenient().doThrow(new NotFoundBusinessException("usuario"))
                    .when(validationSupport).getRequiredUser(allocationInput.getUserId(), "allocationUserId");

            assertThatThrownBy(() -> shiftOpeningService.openShift(request))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(allocationRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class OpeningNozzleReadings {

        @Test
        void shouldRegisterOpeningReadingWithNozzleMeter() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            Nozzle nozzle = ShiftATestData.nozzle(UUID.randomUUID(), ShiftATestData.NOZZLE_METER);
            when(validationSupport.findNozzlesByStationId(request.getStationId())).thenReturn(List.of(nozzle));
            givenShiftIsSaved();

            ShiftResponseDTO response = shiftOpeningService.openShift(request);

            ArgumentCaptor<NozzleReading> captor = ArgumentCaptor.forClass(NozzleReading.class);
            verify(nozzleReadingRepository).saveAndFlush(captor.capture());
            NozzleReading reading = captor.getValue();
            assertThat(reading.getShift().getId()).isEqualTo(response.getId());
            assertThat(reading.getNozzleId()).isEqualTo(nozzle.getId());
            assertThat(reading.getReadingType()).isEqualTo(NozzleReadingTypeEnum.OPENING);
            assertThat(reading.getMeterReading()).isEqualByComparingTo(ShiftATestData.NOZZLE_METER);
            assertThat(reading.getRecordedBy()).isEqualTo(request.getOpenedBy());
            assertThat(reading.getReadAt()).isNotNull();
        }

        @Test
        void shouldUseZeroMeterWhenNozzleHasNoMeterNumber() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();
            Nozzle nozzle = ShiftATestData.nozzle(UUID.randomUUID(), null);
            when(validationSupport.findNozzlesByStationId(request.getStationId())).thenReturn(List.of(nozzle));
            givenShiftIsSaved();

            shiftOpeningService.openShift(request);

            ArgumentCaptor<NozzleReading> captor = ArgumentCaptor.forClass(NozzleReading.class);
            verify(nozzleReadingRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getMeterReading()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldNotRegisterReadingsWhenStationHasNoNozzles() {
            givenShiftIsSaved();

            shiftOpeningService.openShift(ShiftFixture.openRequest());

            verifyNoInteractions(nozzleReadingRepository);
        }
    }
}
