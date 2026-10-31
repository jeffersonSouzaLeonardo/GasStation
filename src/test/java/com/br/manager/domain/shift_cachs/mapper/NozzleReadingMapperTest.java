package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.NozzleReadingFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NozzleReadingMapperTest {

    private final NozzleReadingMapper mapper = new NozzleReadingMapper();
    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);

    @Test
    void shouldMapInputToEntityLinkingShift() {
        NozzleReadingInputDTO input = NozzleReadingFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), NozzleReadingTypeEnum.CLOSING);

        NozzleReading reading = mapper.toEntity(input, shift);

        assertThat(reading.getId()).isNull();
        assertThat(reading.getShift()).isSameAs(shift);
        assertThat(reading)
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
        NozzleReading reading = NozzleReadingFixture.persistedEntity(shift, UUID.randomUUID(), UUID.randomUUID(), NozzleReadingTypeEnum.OPENING);

        NozzleReadingResponseDTO response = mapper.toResponse(reading);

        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("shiftId")
                .isEqualTo(reading);
    }

    @Test
    void shouldMapNullShiftIdWhenReadingHasNoShift() {
        NozzleReading reading = NozzleReadingFixture.persistedEntity(null, UUID.randomUUID(), UUID.randomUUID(), NozzleReadingTypeEnum.TEST);

        assertThat(mapper.toResponse(reading).getShiftId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
