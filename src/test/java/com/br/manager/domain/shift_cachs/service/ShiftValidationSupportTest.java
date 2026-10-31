package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.operational.repository.PumpRepository;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ShiftValidationSupportTest {

    private static final String FIELD = "cashierUserId";

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PumpRepository pumpRepository;

    @Mock
    private NozzleRepository nozzleRepository;

    @InjectMocks
    private ShiftValidationSupport validationSupport;

    @Nested
    class GetRequiredShift {

        @Test
        void shouldReturnShiftWhenFound() {
            Shift shift = ShiftFixture.openShift();
            when(shiftRepository.findById(shift.getId())).thenReturn(Optional.of(shift));

            assertThat(validationSupport.getRequiredShift(shift.getId())).isSameAs(shift);
        }

        @Test
        void shouldThrowNotFoundWhenShiftDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(shiftRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validationSupport.getRequiredShift(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class EnsureShiftIsOpen {

        @Test
        void shouldAcceptOpenShift() {
            assertThatCode(() -> validationSupport.ensureShiftIsOpen(ShiftFixture.openShift()))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldRejectClosedShift() {
            Shift shift = ShiftFixture.closedShift();

            assertThatThrownBy(() -> validationSupport.ensureShiftIsOpen(shift))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(shift.getId().toString());
        }
    }

    @Nested
    class GetRequiredStation {

        @Test
        void shouldReturnActiveStation() {
            Station station = ShiftATestData.station(UUID.randomUUID(), true);
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));

            assertThat(validationSupport.getRequiredStation(station.getId())).isSameAs(station);
        }

        @Test
        void shouldThrowNotFoundWhenStationDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(stationRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validationSupport.getRequiredStation(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldThrowNotFoundWhenStationIsInactive() {
            Station station = ShiftATestData.station(UUID.randomUUID(), false);
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));

            assertThatThrownBy(() -> validationSupport.getRequiredStation(station.getId()))
                    .isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class GetRequiredUser {

        @Test
        void shouldReturnActiveUser() {
            User user = ShiftATestData.user(UUID.randomUUID(), true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            assertThat(validationSupport.getRequiredUser(user.getId(), FIELD)).isSameAs(user);
        }

        @Test
        void shouldThrowNotFoundMentioningFieldWhenUserDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validationSupport.getRequiredUser(id, FIELD))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString())
                    .hasMessageContaining(FIELD);
        }

        @Test
        void shouldUseDefaultFieldNameWhenFieldIsBlank() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validationSupport.getRequiredUser(id, " "))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageEndingWith("no campo usuário");
        }

        @Test
        void shouldUseDefaultFieldNameWhenFieldIsNull() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validationSupport.getRequiredUser(id, null))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageEndingWith("no campo usuário");
        }

        @Test
        void shouldThrowNotFoundWhenUserIsInactive() {
            User user = ShiftATestData.user(UUID.randomUUID(), false);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> validationSupport.getRequiredUser(user.getId(), FIELD))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(FIELD);
        }
    }

    @Nested
    class FindNozzlesByStationId {

        @Test
        void shouldReturnEmptyWhenThereAreNoActivePumps() {
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(validationSupport.findNozzlesByStationId(UUID.randomUUID())).isEmpty();
            verify(nozzleRepository, never()).findByPumpIdAndActiveTrue(any());
        }

        @Test
        void shouldCollectActiveNozzlesOfAllActivePumps() {
            Pump firstPump = ShiftATestData.pump();
            Pump secondPump = ShiftATestData.pump();
            Nozzle firstNozzle = ShiftATestData.nozzle(firstPump.getId(), ShiftATestData.NOZZLE_METER);
            Nozzle secondNozzle = ShiftATestData.nozzle(secondPump.getId(), ShiftATestData.NOZZLE_METER);
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of(firstPump, secondPump));
            when(nozzleRepository.findByPumpIdAndActiveTrue(firstPump.getId())).thenReturn(List.of(firstNozzle));
            when(nozzleRepository.findByPumpIdAndActiveTrue(secondPump.getId())).thenReturn(List.of(secondNozzle));

            assertThat(validationSupport.findNozzlesByStationId(UUID.randomUUID()))
                    .containsExactly(firstNozzle, secondNozzle);
        }

        @Test
        void shouldNotRepeatNozzleReturnedForMoreThanOnePump() {
            Pump firstPump = ShiftATestData.pump();
            Pump secondPump = ShiftATestData.pump();
            Nozzle nozzle = ShiftATestData.nozzle(firstPump.getId(), ShiftATestData.NOZZLE_METER);
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of(firstPump, secondPump));
            when(nozzleRepository.findByPumpIdAndActiveTrue(firstPump.getId())).thenReturn(List.of(nozzle));
            when(nozzleRepository.findByPumpIdAndActiveTrue(secondPump.getId())).thenReturn(List.of(nozzle));

            assertThat(validationSupport.findNozzlesByStationId(UUID.randomUUID())).containsExactly(nozzle);
        }
    }

    @Nested
    class ValidateNozzleBelongsToShiftStation {

        @Test
        void shouldAcceptNozzleFoundForStation() {
            Pump pump = ShiftATestData.pump();
            Nozzle nozzle = ShiftATestData.nozzle(pump.getId(), ShiftATestData.NOZZLE_METER);
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of(pump));
            when(nozzleRepository.findByPumpIdAndActiveTrue(pump.getId())).thenReturn(List.of(nozzle));

            assertThatCode(() -> validationSupport.validateNozzleBelongsToShiftStation(UUID.randomUUID(), nozzle.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldRejectNozzleNotFoundForStation() {
            UUID stationId = UUID.randomUUID();
            UUID nozzleId = UUID.randomUUID();
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThatThrownBy(() -> validationSupport.validateNozzleBelongsToShiftStation(stationId, nozzleId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(nozzleId.toString())
                    .hasMessageContaining(stationId.toString());
        }
    }
}
