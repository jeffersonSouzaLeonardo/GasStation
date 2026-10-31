package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.entity.Tank;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Tanque.
 */
public final class TankFixture {

    public static final String CODE = "TQ-01";
    public static final String OTHER_CODE = "TQ-02";
    public static final BigDecimal CAPACITY = new BigDecimal("15000.5");
    public static final BigDecimal DEAD_STOCK = new BigDecimal("150.25");
    public static final BigDecimal CURRENT_BOOK = new BigDecimal("8000.75");

    private TankFixture() {
    }

    public static TankInputDTO validInput(UUID productId) {
        TankInputDTO input = new TankInputDTO();
        input.setCode(CODE);
        input.setProductId(productId);
        input.setCapacityLiters(CAPACITY);
        input.setDeadStockLiters(DEAD_STOCK);
        input.setCurrentBookLiters(CURRENT_BOOK);
        input.setActive(true);
        return input;
    }

    public static TankInputDTO validInput(UUID productId, String code) {
        TankInputDTO input = validInput(productId);
        input.setCode(code);
        return input;
    }

    public static Tank persistedEntity() {
        Tank tank = new Tank();
        tank.setId(UUID.randomUUID());
        tank.setCode(CODE);
        tank.setProductId(UUID.randomUUID());
        tank.setCapacityLiters(CAPACITY);
        tank.setDeadStockLiters(DEAD_STOCK);
        tank.setCurrentBookLiters(CURRENT_BOOK);
        tank.setActive(true);
        return tank;
    }
}
