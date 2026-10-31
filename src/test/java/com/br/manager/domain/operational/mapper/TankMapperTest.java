package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.fixture.TankFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TankMapperTest {

    private final TankMapper mapper = new TankMapper();

    @Test
    void shouldMapInputToEntity() {
        TankInputDTO input = TankFixture.validInput(UUID.randomUUID());
        input.setId(UUID.randomUUID());

        Tank tank = mapper.tankInputToTankEntity(input);

        assertThat(tank)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "code", "productId", "capacityLiters", "deadStockLiters", "currentBookLiters", "active")
                .isEqualTo(input);
    }

    @Test
    void shouldDefaultActiveToTrueWhenInputActiveIsNull() {
        TankInputDTO input = TankFixture.validInput(UUID.randomUUID());
        input.setActive(null);

        Tank tank = mapper.tankInputToTankEntity(input);

        assertThat(tank.getActive()).isTrue();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.tankInputToTankEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Tank tank = TankFixture.persistedEntity();

        TankResponseDTO response = mapper.tankEntityToTankResponseDTO(tank);

        assertThat(response).usingRecursiveComparison().isEqualTo(tank);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.tankEntityToTankResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Tank> tanks = List.of(TankFixture.persistedEntity(), TankFixture.persistedEntity());

        List<TankResponseDTO> responses = mapper.listTankEntityToListTankResponseDTO(tanks);

        assertThat(responses)
                .extracting(TankResponseDTO::getId)
                .containsExactly(tanks.get(0).getId(), tanks.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listTankEntityToListTankResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateOnlyNonNullFields() {
        Tank tank = TankFixture.persistedEntity();
        TankInputDTO input = new TankInputDTO();
        input.setCode(TankFixture.OTHER_CODE);
        input.setCapacityLiters(BigDecimal.TEN);
        input.setActive(false);

        mapper.updateTankFromDto(input, tank);

        assertThat(tank.getCode()).isEqualTo(TankFixture.OTHER_CODE);
        assertThat(tank.getCapacityLiters()).isEqualTo(BigDecimal.TEN);
        assertThat(tank.getActive()).isFalse();
        assertThat(tank.getProductId()).isNotNull();
        assertThat(tank.getDeadStockLiters()).isEqualTo(TankFixture.DEAD_STOCK);
        assertThat(tank.getCurrentBookLiters()).isEqualTo(TankFixture.CURRENT_BOOK);
    }

    @Test
    void shouldUpdateAllFieldsWhenAllAreInformed() {
        Tank tank = TankFixture.persistedEntity();
        UUID originalId = tank.getId();
        TankInputDTO input = TankFixture.validInput(UUID.randomUUID(), TankFixture.OTHER_CODE);
        input.setDeadStockLiters(BigDecimal.ONE);
        input.setCurrentBookLiters(BigDecimal.TEN);

        mapper.updateTankFromDto(input, tank);

        assertThat(tank.getId()).isEqualTo(originalId);
        assertThat(tank.getProductId()).isEqualTo(input.getProductId());
        assertThat(tank.getDeadStockLiters()).isEqualTo(BigDecimal.ONE);
        assertThat(tank.getCurrentBookLiters()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Tank tank = TankFixture.persistedEntity();

        mapper.updateTankFromDto(null, tank);

        assertThat(tank.getCode()).isEqualTo(TankFixture.CODE);
    }
}
