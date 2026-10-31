package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionResponseDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.CashSessionFixture;
import com.br.manager.domain.shift_cachs.mapper.CashSessionMapper;
import com.br.manager.domain.shift_cachs.repository.CashSessionRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashSessionServiceTest {

    private static final String VIOLATION_MESSAGE = "O fundo de troco é obrigatório.";

    @Mock
    private CashSessionRepository cashSessionRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftValidationSupport validationSupport;

    @Spy
    private CashSessionMapper cashSessionMapper = new CashSessionMapper();

    @InjectMocks
    private CashSessionService cashSessionService;

    private final Shift openShift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);

    private void returnSavedSession() {
        when(cashSessionRepository.saveAndFlush(any(CashSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private CashSession captureSavedSession() {
        ArgumentCaptor<CashSession> captor = ArgumentCaptor.forClass(CashSession.class);
        verify(cashSessionRepository).saveAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class Open {

        private final CashSessionInputDTO input = CashSessionFixture.validInput(openShift.getId(), UUID.randomUUID());

        @Test
        void shouldOpenSessionWithGeneratedIdOpenStatusAndOpeningDate() {
            when(shiftRepository.findByIdAndStatus(openShift.getId(), ShiftStatusEnum.OPEN)).thenReturn(Optional.of(openShift));
            returnSavedSession();

            CashSessionResponseDTO response = cashSessionService.open(input);

            CashSession saved = captureSavedSession();
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(CashSessionStatusEnum.OPEN);
            assertThat(saved.getOpenedAt()).isNotNull();
            assertThat(saved.getShift()).isSameAs(openShift);
            assertThat(response.getId()).isEqualTo(saved.getId());
            assertThat(response.getShiftId()).isEqualTo(openShift.getId());
            assertThat(response.getOpeningCash()).isEqualTo(CashSessionFixture.OPENING_CASH);
        }

        @Test
        void shouldValidateCashierUser() {
            when(shiftRepository.findByIdAndStatus(openShift.getId(), ShiftStatusEnum.OPEN)).thenReturn(Optional.of(openShift));
            returnSavedSession();

            cashSessionService.open(input);

            verify(validationSupport).getRequiredUser(input.getCashierUserId(), "cashierUserId");
        }

        @Test
        void shouldThrowBusinessExceptionWhenPayloadIsNull() {
            assertThatThrownBy(() -> cashSessionService.open(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("obrigatório");
            verifyNoInteractions(shiftRepository, cashSessionRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenShiftIsNotOpen() {
            when(shiftRepository.findByIdAndStatus(openShift.getId(), ShiftStatusEnum.OPEN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashSessionService.open(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(openShift.getId().toString());
            verify(cashSessionRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenCashierUserDoesNotExist() {
            when(shiftRepository.findByIdAndStatus(openShift.getId(), ShiftStatusEnum.OPEN)).thenReturn(Optional.of(openShift));
            when(validationSupport.getRequiredUser(input.getCashierUserId(), "cashierUserId"))
                    .thenThrow(new NotFoundBusinessException("usuario"));

            assertThatThrownBy(() -> cashSessionService.open(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(cashSessionRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowBusinessExceptionWhenCashierAlreadyHasOpenSession() {
            when(shiftRepository.findByIdAndStatus(openShift.getId(), ShiftStatusEnum.OPEN)).thenReturn(Optional.of(openShift));
            when(cashSessionRepository.existsByCashierUserIdAndStatus(input.getCashierUserId(), CashSessionStatusEnum.OPEN))
                    .thenReturn(true);

            assertThatThrownBy(() -> cashSessionService.open(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(input.getCashierUserId().toString());
            verify(cashSessionRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Close {

        private final CashSession openSession = CashSessionFixture.openEntity(openShift);

        @Test
        void shouldCloseSessionApplyingTotals() {
            when(cashSessionRepository.findByIdAndStatus(openSession.getId(), CashSessionStatusEnum.OPEN)).thenReturn(Optional.of(openSession));
            returnSavedSession();

            CashSessionResponseDTO response = cashSessionService.close(openSession.getId(), CashSessionFixture.closeInput());

            CashSession saved = captureSavedSession();
            assertThat(saved.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
            assertThat(saved.getClosedAt()).isNotNull();
            assertThat(saved.getTotalCash()).isEqualTo(CashSessionFixture.TOTAL_CASH);
            assertThat(saved.getTotalCard()).isEqualTo(CashSessionFixture.TOTAL_CARD);
            assertThat(saved.getTotalPix()).isEqualTo(CashSessionFixture.TOTAL_PIX);
            assertThat(saved.getTotalWithdrawal()).isEqualTo(CashSessionFixture.TOTAL_WITHDRAWAL);
            assertThat(saved.getNotes()).isEqualTo(CashSessionFixture.CLOSING_NOTES);
            assertThat(response.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
        }

        @Test
        void shouldKeepOriginalNotesWhenCloseNotesAreNull() {
            when(cashSessionRepository.findByIdAndStatus(openSession.getId(), CashSessionStatusEnum.OPEN)).thenReturn(Optional.of(openSession));
            returnSavedSession();
            var closeInput = CashSessionFixture.closeInput();
            closeInput.setNotes(null);

            cashSessionService.close(openSession.getId(), closeInput);

            assertThat(captureSavedSession().getNotes()).isEqualTo(CashSessionFixture.NOTES);
        }

        @Test
        void shouldCloseSessionWithoutTotalsWhenPayloadIsNull() {
            when(cashSessionRepository.findByIdAndStatus(openSession.getId(), CashSessionStatusEnum.OPEN)).thenReturn(Optional.of(openSession));
            returnSavedSession();

            cashSessionService.close(openSession.getId(), null);

            CashSession saved = captureSavedSession();
            assertThat(saved.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
            assertThat(saved.getClosedAt()).isNotNull();
            assertThat(saved.getTotalCash()).isNull();
        }

        @Test
        void shouldThrowNotFoundWhenOpenSessionDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(cashSessionRepository.findByIdAndStatus(id, CashSessionStatusEnum.OPEN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashSessionService.close(id, CashSessionFixture.closeInput()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
            verify(cashSessionRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindSessionById() {
            CashSession session = CashSessionFixture.openEntity(openShift);
            when(cashSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

            CashSessionResponseDTO response = cashSessionService.findById(session.getId());

            assertThat(response.getId()).isEqualTo(session.getId());
            assertThat(response.getShiftId()).isEqualTo(openShift.getId());
        }

        @Test
        void shouldThrowNotFoundWhenSessionIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(cashSessionRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashSessionService.findById(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListSessionsOfShift() {
            List<CashSession> sessions = List.of(CashSessionFixture.openEntity(openShift), CashSessionFixture.closedEntity(openShift));
            when(cashSessionRepository.findByShift_IdOrderByOpenedAt(openShift.getId())).thenReturn(sessions);

            List<CashSessionResponseDTO> responses = cashSessionService.findByShift(openShift.getId());

            assertThat(responses)
                    .extracting(CashSessionResponseDTO::getId)
                    .containsExactly(sessions.get(0).getId(), sessions.get(1).getId());
        }
    }

    @Nested
    class SaveAll {

        private final CashSessionSyncDTO sync = CashSessionFixture.closedSyncInput(UUID.randomUUID(), openShift.getId(), UUID.randomUUID());

        @Test
        void shouldIgnoreNullList() {
            cashSessionService.saveAll(null);

            verifyNoInteractions(cashSessionRepository, shiftRepository);
        }

        @Test
        void shouldIgnoreEmptyList() {
            cashSessionService.saveAll(List.of());

            verifyNoInteractions(cashSessionRepository, shiftRepository);
        }

        @Test
        void shouldSkipNullItems() {
            cashSessionService.saveAll(Collections.singletonList(null));

            verifyNoInteractions(cashSessionRepository, shiftRepository);
        }

        @Test
        void shouldInsertNewSessionLinkedToShift() {
            when(cashSessionRepository.findById(sync.getId())).thenReturn(Optional.empty());
            when(shiftRepository.findById(openShift.getId())).thenReturn(Optional.of(openShift));

            cashSessionService.saveAll(List.of(sync));

            CashSession saved = captureSavedSession();
            assertThat(saved.getId()).isEqualTo(sync.getId());
            assertThat(saved.getShift()).isSameAs(openShift);
            assertThat(saved.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
        }

        @Test
        void shouldUpdateExistingSessionIdempotently() {
            CashSession existing = CashSessionFixture.openEntity(openShift);
            existing.setId(sync.getId());
            when(cashSessionRepository.findById(sync.getId())).thenReturn(Optional.of(existing));

            cashSessionService.saveAll(List.of(sync));

            CashSession saved = captureSavedSession();
            assertThat(saved).isSameAs(existing);
            assertThat(saved.getStatus()).isEqualTo(CashSessionStatusEnum.CLOSED);
            assertThat(saved.getTotalCash()).isEqualTo(CashSessionFixture.TOTAL_CASH);
            verifyNoInteractions(shiftRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenShiftOfNewSessionDoesNotExist() {
            when(cashSessionRepository.findById(sync.getId())).thenReturn(Optional.empty());
            when(shiftRepository.findById(openShift.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cashSessionService.saveAll(List.of(sync)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(openShift.getId().toString())
                    .hasMessageContaining(sync.getId().toString());
            verify(cashSessionRepository, never()).saveAndFlush(any());
        }

        @Test
        @SuppressWarnings("unchecked")
        void shouldWrapConstraintViolationInBusinessException() {
            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn(VIOLATION_MESSAGE);
            ConstraintViolationException violationException = new ConstraintViolationException(Set.of(violation));
            when(cashSessionRepository.findById(sync.getId())).thenReturn(Optional.empty());
            when(shiftRepository.findById(openShift.getId())).thenReturn(Optional.of(openShift));
            when(cashSessionRepository.saveAndFlush(any(CashSession.class))).thenThrow(violationException);

            assertThatThrownBy(() -> cashSessionService.saveAll(List.of(sync)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(VIOLATION_MESSAGE);
        }
    }
}
