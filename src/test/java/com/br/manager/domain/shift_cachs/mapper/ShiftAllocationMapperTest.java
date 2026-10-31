package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.fixture.ShiftAllocationFixture;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShiftAllocationMapperTest {

    private final ShiftAllocationMapper mapper = new ShiftAllocationMapper();

    @Test
    void shouldMapInputToEntityLinkingShift() {
        Shift shift = ShiftFixture.openShift();
        ShiftAllocationInputDTO input = ShiftAllocationFixture.validInput();

        ShiftAllocation allocation = mapper.toEntity(input, shift);

        assertThat(allocation.getId()).isNull();
        assertThat(allocation.getShift()).isSameAs(shift);
        assertThat(allocation.getUserId()).isEqualTo(input.getUserId());
        assertThat(allocation.getRole()).isEqualTo(ShiftAllocationFixture.ROLE);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null, ShiftFixture.openShift())).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Shift shift = ShiftFixture.openShift();
        ShiftAllocation allocation = ShiftAllocationFixture.persistedEntity(shift, UUID.randomUUID());

        ShiftAllocationResponseDTO response = mapper.toResponse(allocation);

        assertThat(response.getId()).isEqualTo(allocation.getId());
        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response.getUserId()).isEqualTo(allocation.getUserId());
        assertThat(response.getRole()).isEqualTo(ShiftAllocationFixture.ROLE);
    }

    @Test
    void shouldMapNullShiftIdWhenAllocationHasNoShift() {
        ShiftAllocation allocation = ShiftAllocationFixture.persistedEntity(null, UUID.randomUUID());

        ShiftAllocationResponseDTO response = mapper.toResponse(allocation);

        assertThat(response.getShiftId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
