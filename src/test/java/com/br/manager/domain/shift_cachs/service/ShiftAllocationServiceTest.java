package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.fixture.ShiftAllocationFixture;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.mapper.ShiftAllocationMapper;
import com.br.manager.domain.shift_cachs.repository.ShiftAllocationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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
class ShiftAllocationServiceTest {

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private ShiftAllocationRepository allocationRepository;

    @Spy
    private ShiftAllocationMapper allocationMapper = new ShiftAllocationMapper();

    @InjectMocks
    private ShiftAllocationService shiftAllocationService;

    @Nested
    class AddOrUpdateAllocation {

        @Test
        void shouldCreateAllocationWhenUserIsNotAllocatedYet() {
            Shift shift = ShiftFixture.openShift();
            ShiftAllocationInputDTO input = ShiftAllocationFixture.validInput();
            when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
            when(allocationRepository.findByShiftIdAndUserId(shift.getId(), input.getUserId())).thenReturn(Optional.empty());
            when(allocationRepository.saveAndFlush(any(ShiftAllocation.class))).thenAnswer(invocation -> {
                ShiftAllocation allocation = invocation.getArgument(0);
                allocation.setId(UUID.randomUUID());
                return allocation;
            });

            ShiftAllocationResponseDTO response = shiftAllocationService.addOrUpdateAllocation(shift.getId(), input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getShiftId()).isEqualTo(shift.getId());
            assertThat(response.getUserId()).isEqualTo(input.getUserId());
            assertThat(response.getRole()).isEqualTo(ShiftAllocationFixture.ROLE);
        }

        @Test
        void shouldUpdateRoleWhenUserIsAlreadyAllocated() {
            Shift shift = ShiftFixture.openShift();
            ShiftAllocation existing = ShiftAllocationFixture.persistedEntity(shift, UUID.randomUUID());
            ShiftAllocationInputDTO input = ShiftAllocationFixture.input(existing.getUserId(), ShiftAllocationFixture.OTHER_ROLE);
            when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
            when(allocationRepository.findByShiftIdAndUserId(shift.getId(), existing.getUserId())).thenReturn(Optional.of(existing));
            when(allocationRepository.saveAndFlush(any(ShiftAllocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ShiftAllocationResponseDTO response = shiftAllocationService.addOrUpdateAllocation(shift.getId(), input);

            ArgumentCaptor<ShiftAllocation> captor = ArgumentCaptor.forClass(ShiftAllocation.class);
            verify(allocationRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue()).isSameAs(existing);
            assertThat(captor.getValue().getRole()).isEqualTo(ShiftAllocationFixture.OTHER_ROLE);
            assertThat(response.getId()).isEqualTo(existing.getId());
            verify(allocationMapper, never()).toEntity(any(), any());
        }

        @Test
        void shouldRejectNullRequest() {
            assertThatThrownBy(() -> shiftAllocationService.addOrUpdateAllocation(UUID.randomUUID(), null))
                    .isInstanceOf(BusinessException.class);
            verifyNoInteractions(validationSupport, allocationRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenShiftIsUnknown() {
            UUID shiftId = UUID.randomUUID();
            when(validationSupport.getRequiredShift(shiftId)).thenThrow(new NotFoundBusinessException("turno"));

            assertThatThrownBy(() -> shiftAllocationService.addOrUpdateAllocation(shiftId, ShiftAllocationFixture.validInput()))
                    .isInstanceOf(NotFoundBusinessException.class);
            verifyNoInteractions(allocationRepository);
        }

        @Test
        void shouldPropagateBusinessErrorWhenShiftIsClosed() {
            Shift shift = ShiftFixture.closedShift();
            when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
            doThrow(new BusinessException("fechado")).when(validationSupport).ensureShiftIsOpen(shift);

            assertThatThrownBy(() -> shiftAllocationService.addOrUpdateAllocation(shift.getId(), ShiftAllocationFixture.validInput()))
                    .isInstanceOf(BusinessException.class);
            verifyNoInteractions(allocationRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenUserIsUnknown() {
            Shift shift = ShiftFixture.openShift();
            ShiftAllocationInputDTO input = ShiftAllocationFixture.validInput();
            when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
            when(validationSupport.getRequiredUser(input.getUserId(), "allocationUserId"))
                    .thenThrow(new NotFoundBusinessException("usuario"));

            assertThatThrownBy(() -> shiftAllocationService.addOrUpdateAllocation(shift.getId(), input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verifyNoInteractions(allocationRepository);
        }
    }
}
