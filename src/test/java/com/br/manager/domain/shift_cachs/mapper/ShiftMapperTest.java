package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShiftMapperTest {

    private final ShiftMapper mapper = new ShiftMapper();

    @Nested
    class OpenRequestToEntity {

        @Test
        void shouldMapOpenRequestFields() {
            ShiftOpenRequestDTO request = ShiftFixture.openRequest();

            Shift shift = mapper.toEntity(request);

            assertThat(shift.getId()).isNull();
            assertThat(shift.getStationId()).isEqualTo(request.getStationId());
            assertThat(shift.getCashierUserId()).isEqualTo(request.getCashierUserId());
            assertThat(shift.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
            assertThat(shift.getOpenedBy()).isEqualTo(request.getOpenedBy());
            assertThat(shift.getNotes()).isEqualTo(ShiftFixture.NOTES);
        }

        @Test
        void shouldNotSetStatusNorOpenedAt() {
            Shift shift = mapper.toEntity(ShiftFixture.openRequest());

            assertThat(shift.getStatus()).isNull();
            assertThat(shift.getOpenedAt()).isNull();
        }

        @Test
        void shouldReturnNullWhenOpenRequestIsNull() {
            assertThat(mapper.toEntity((ShiftOpenRequestDTO) null)).isNull();
        }
    }

    @Nested
    class SyncToEntity {

        @Test
        void shouldMapSyncFieldsKeepingId() {
            ShiftSyncDTO dto = ShiftFixture.closedSync(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            Shift shift = mapper.toEntity(dto);

            assertThat(shift.getId()).isEqualTo(dto.getId());
            assertThat(shift.getStationId()).isEqualTo(dto.getStationId());
            assertThat(shift.getName()).isEqualTo(ShiftFixture.NAME);
            assertThat(shift.getOpenedAt()).isEqualTo(ShiftFixture.OPENED_AT);
            assertThat(shift.getClosedAt()).isEqualTo(ShiftFixture.CLOSED_AT);
            assertThat(shift.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
            assertThat(shift.getOpenedBy()).isEqualTo(dto.getOpenedBy());
            assertThat(shift.getClosedBy()).isEqualTo(dto.getClosedBy());
            assertThat(shift.getNotes()).isEqualTo(ShiftFixture.SYNC_NOTES);
        }

        @Test
        void shouldUseOpenedByAsCashierAndZeroOpeningCash() {
            ShiftSyncDTO dto = ShiftFixture.openSync();

            Shift shift = mapper.toEntity(dto);

            assertThat(shift.getCashierUserId()).isEqualTo(dto.getOpenedBy());
            assertThat(shift.getOpeningCash()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldReturnNullWhenSyncIsNull() {
            assertThat(mapper.toEntity((ShiftSyncDTO) null)).isNull();
        }
    }

    @Nested
    class UpdateFromSync {

        @Test
        void shouldOverwriteSyncFieldsPreservingIdCashierAndOpeningCash() {
            Shift shift = ShiftFixture.openShift();
            UUID originalId = shift.getId();
            UUID originalCashier = shift.getCashierUserId();
            ShiftSyncDTO dto = ShiftFixture.closedSync(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            mapper.updateFromSync(dto, shift);

            assertThat(shift.getId()).isEqualTo(originalId);
            assertThat(shift.getCashierUserId()).isEqualTo(originalCashier);
            assertThat(shift.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
            assertThat(shift.getStationId()).isEqualTo(dto.getStationId());
            assertThat(shift.getName()).isEqualTo(ShiftFixture.NAME);
            assertThat(shift.getOpenedAt()).isEqualTo(ShiftFixture.OPENED_AT);
            assertThat(shift.getClosedAt()).isEqualTo(ShiftFixture.CLOSED_AT);
            assertThat(shift.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
            assertThat(shift.getOpenedBy()).isEqualTo(dto.getOpenedBy());
            assertThat(shift.getClosedBy()).isEqualTo(dto.getClosedBy());
            assertThat(shift.getNotes()).isEqualTo(ShiftFixture.SYNC_NOTES);
        }

        @Test
        void shouldIgnoreNullDto() {
            Shift shift = ShiftFixture.openShift();

            mapper.updateFromSync(null, shift);

            assertThat(shift.getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
            assertThat(shift.getNotes()).isEqualTo(ShiftFixture.NOTES);
        }

        @Test
        void shouldIgnoreNullShift() {
            mapper.updateFromSync(ShiftFixture.openSync(), null);
        }
    }

    @Nested
    class ToResponse {

        @Test
        void shouldMapEntityToResponse() {
            Shift shift = ShiftFixture.closedShift();

            ShiftResponseDTO response = mapper.toResponse(shift);

            assertThat(response.getId()).isEqualTo(shift.getId());
            assertThat(response.getStationId()).isEqualTo(shift.getStationId());
            assertThat(response.getCashierUserId()).isEqualTo(shift.getCashierUserId());
            assertThat(response.getOpenedAt()).isEqualTo(ShiftFixture.OPENED_AT);
            assertThat(response.getClosedAt()).isEqualTo(ShiftFixture.CLOSED_AT);
            assertThat(response.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
            assertThat(response.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
            assertThat(response.getOpenedBy()).isEqualTo(shift.getOpenedBy());
            assertThat(response.getClosedBy()).isEqualTo(shift.getClosedBy());
            assertThat(response.getNotes()).isEqualTo(ShiftFixture.NOTES);
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(mapper.toResponse(null)).isNull();
        }
    }
}
