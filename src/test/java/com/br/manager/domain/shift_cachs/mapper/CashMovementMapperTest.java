package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.CashMovementFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CashMovementMapperTest {

    private final CashMovementMapper mapper = new CashMovementMapper();
    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);

    @Test
    void shouldMapInputToEntityLinkingShift() {
        CashMovementInputDTO input = CashMovementFixture.completeInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        CashMovement movement = mapper.toEntity(input, shift);

        assertThat(movement.getId()).isNull();
        assertThat(movement.getShift()).isSameAs(shift);
        assertThat(movement)
                .usingRecursiveComparison()
                .ignoringFields("id", "shift")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullEntityWhenInputIsNull() {
        assertThat(mapper.toEntity(null, shift)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithShiftId() {
        CashMovement movement = CashMovementFixture.persistedEntity(shift);

        CashMovementResponseDTO response = mapper.toResponse(movement);

        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("shiftId")
                .isEqualTo(movement);
    }

    @Test
    void shouldMapNullShiftIdWhenMovementHasNoShift() {
        CashMovement movement = CashMovementFixture.persistedEntity(null);

        assertThat(mapper.toResponse(movement).getShiftId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
