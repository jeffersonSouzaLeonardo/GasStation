package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftSyncServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Spy
    private ShiftMapper shiftMapper = new ShiftMapper();

    @InjectMocks
    private ShiftSyncService shiftSyncService;

    @Test
    void shouldInsertShiftWhenItDoesNotExist() {
        ShiftSyncDTO dto = ShiftFixture.openSync();
        when(shiftRepository.findById(dto.getId())).thenReturn(Optional.empty());

        shiftSyncService.saveAll(List.of(dto));

        ArgumentCaptor<Shift> captor = ArgumentCaptor.forClass(Shift.class);
        verify(shiftRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(dto.getId());
        assertThat(captor.getValue().getCashierUserId()).isEqualTo(dto.getOpenedBy());
        assertThat(captor.getValue().getOpeningCash()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captor.getValue().getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
    }

    @Test
    void shouldUpdateExistingShiftKeepingOpeningCash() {
        Shift existing = ShiftFixture.openShift();
        ShiftSyncDTO dto = ShiftFixture.closedSync(existing.getId(), existing.getStationId(), existing.getOpenedBy());
        when(shiftRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

        shiftSyncService.saveAll(List.of(dto));

        ArgumentCaptor<Shift> captor = ArgumentCaptor.forClass(Shift.class);
        verify(shiftRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue()).isSameAs(existing);
        assertThat(existing.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
        assertThat(existing.getClosedAt()).isEqualTo(ShiftFixture.CLOSED_AT);
        assertThat(existing.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
    }

    @Test
    void shouldIgnoreNullList() {
        shiftSyncService.saveAll(null);

        verifyNoInteractions(shiftRepository);
    }

    @Test
    void shouldIgnoreEmptyList() {
        shiftSyncService.saveAll(List.of());

        verifyNoInteractions(shiftRepository);
    }

    @Test
    void shouldSkipNullEntries() {
        ShiftSyncDTO dto = ShiftFixture.openSync();
        when(shiftRepository.findById(dto.getId())).thenReturn(Optional.empty());

        shiftSyncService.saveAll(Arrays.asList(null, dto));

        verify(shiftRepository, times(1)).saveAndFlush(any(Shift.class));
    }

    @Test
    void shouldWrapConstraintViolationInBusinessException() {
        ShiftSyncDTO dto = ShiftFixture.openSync();
        when(shiftRepository.findById(dto.getId())).thenReturn(Optional.empty());
        when(shiftRepository.saveAndFlush(any(Shift.class))).thenThrow(invalidShiftViolation());

        assertThatThrownBy(() -> shiftSyncService.saveAll(List.of(dto)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O ID do posto é obrigatório.");
    }

    private static ConstraintViolationException invalidShiftViolation() {
        Shift invalid = ShiftFixture.openShift();
        invalid.setStationId(null);
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Shift>> violations = validator.validate(invalid);
            return new ConstraintViolationException(violations);
        }
    }
}
