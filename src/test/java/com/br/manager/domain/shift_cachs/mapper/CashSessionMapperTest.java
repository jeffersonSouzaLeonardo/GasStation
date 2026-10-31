package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionResponseDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.CashSessionFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CashSessionMapperTest {

    private final CashSessionMapper mapper = new CashSessionMapper();
    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);

    @Nested
    class InputToEntity {

        @Test
        void shouldMapInputToEntityLinkingShift() {
            CashSessionInputDTO input = CashSessionFixture.validInput(shift.getId(), UUID.randomUUID());

            CashSession session = mapper.toEntity(input, shift);

            assertThat(session.getShift()).isSameAs(shift);
            assertThat(session.getCashierUserId()).isEqualTo(input.getCashierUserId());
            assertThat(session.getRole()).isEqualTo(CashSessionFixture.ROLE);
            assertThat(session.getOpeningCash()).isEqualTo(CashSessionFixture.OPENING_CASH);
            assertThat(session.getNotes()).isEqualTo(CashSessionFixture.NOTES);
        }

        @Test
        void shouldLeaveIdStatusAndOpenedAtUnsetWhenMappingInput() {
            CashSession session = mapper.toEntity(CashSessionFixture.validInput(shift.getId(), UUID.randomUUID()), shift);

            assertThat(session.getId()).isNull();
            assertThat(session.getStatus()).isNull();
            assertThat(session.getOpenedAt()).isNull();
        }

        @Test
        void shouldReturnNullWhenInputIsNull() {
            assertThat(mapper.toEntity((CashSessionInputDTO) null, shift)).isNull();
        }
    }

    @Nested
    class SyncToEntity {

        @Test
        void shouldMapSyncDtoToEntityKeepingPdvId() {
            CashSessionSyncDTO sync = CashSessionFixture.closedSyncInput(UUID.randomUUID(), shift.getId(), UUID.randomUUID());

            CashSession session = mapper.toEntity(sync, shift);

            assertThat(session.getShift()).isSameAs(shift);
            assertThat(session)
                    .usingRecursiveComparison()
                    .ignoringFields("shift", "createdAt", "updatedAt", "deletedAt")
                    .isEqualTo(sync);
        }

        @Test
        void shouldReturnNullWhenSyncDtoIsNull() {
            assertThat(mapper.toEntity((CashSessionSyncDTO) null, shift)).isNull();
        }
    }

    @Nested
    class UpdateFromSync {

        @Test
        void shouldOverwriteMutableFieldsKeepingIdAndShift() {
            CashSession session = CashSessionFixture.openEntity(shift);
            UUID originalId = session.getId();
            CashSessionSyncDTO sync = CashSessionFixture.closedSyncInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            mapper.updateFromSync(sync, session);

            assertThat(session.getId()).isEqualTo(originalId);
            assertThat(session.getShift()).isSameAs(shift);
            assertThat(session.getCashierUserId()).isEqualTo(sync.getCashierUserId());
            assertThat(session.getRole()).isEqualTo(CashierRoleEnum.PUMP_ATTENDANT);
            assertThat(session.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
            assertThat(session.getOpenedAt()).isEqualTo(CashSessionFixture.OPENED_AT);
            assertThat(session.getClosedAt()).isEqualTo(CashSessionFixture.CLOSED_AT);
            assertThat(session.getTotalCash()).isEqualTo(CashSessionFixture.TOTAL_CASH);
            assertThat(session.getTotalCard()).isEqualTo(CashSessionFixture.TOTAL_CARD);
            assertThat(session.getTotalPix()).isEqualTo(CashSessionFixture.TOTAL_PIX);
            assertThat(session.getTotalWithdrawal()).isEqualTo(CashSessionFixture.TOTAL_WITHDRAWAL);
            assertThat(session.getNotes()).isEqualTo(CashSessionFixture.SYNC_NOTES);
        }

        @Test
        void shouldIgnoreNullSyncDto() {
            CashSession session = CashSessionFixture.openEntity(shift);

            mapper.updateFromSync(null, session);

            assertThat(session.getStatus()).isEqualTo(CashSessionStatusEnum.OPEN);
            assertThat(session.getNotes()).isEqualTo(CashSessionFixture.NOTES);
        }

        @Test
        void shouldIgnoreNullSession() {
            CashSessionSyncDTO sync = CashSessionFixture.closedSyncInput(UUID.randomUUID(), shift.getId(), UUID.randomUUID());

            mapper.updateFromSync(sync, null);

            assertThat(sync.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
        }
    }

    @Nested
    class EntityToResponse {

        @Test
        void shouldMapEntityToResponseWithShiftId() {
            CashSession session = CashSessionFixture.closedEntity(shift);

            CashSessionResponseDTO response = mapper.toResponse(session);

            assertThat(response.getShiftId()).isEqualTo(shift.getId());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("shiftId")
                    .isEqualTo(session);
        }

        @Test
        void shouldMapNullShiftIdWhenSessionHasNoShift() {
            CashSession session = CashSessionFixture.openEntity(null);

            assertThat(mapper.toResponse(session).getShiftId()).isNull();
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(mapper.toResponse(null)).isNull();
        }

        @Test
        void shouldMapEntityListPreservingOrder() {
            List<CashSession> sessions = List.of(CashSessionFixture.openEntity(shift), CashSessionFixture.closedEntity(shift));

            List<CashSessionResponseDTO> responses = mapper.toResponseList(sessions);

            assertThat(responses)
                    .extracting(CashSessionResponseDTO::getId)
                    .containsExactly(sessions.get(0).getId(), sessions.get(1).getId());
        }

        @Test
        void shouldReturnEmptyListWhenEntityListIsNull() {
            assertThat(mapper.toResponseList(null)).isEmpty();
        }
    }
}
