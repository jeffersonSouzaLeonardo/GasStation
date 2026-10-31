package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.entity.StationProduct;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Produto do Posto.
 */
public final class StationProductFixture {

    public static final BigDecimal MIN_STOCK = new BigDecimal("500.25");
    public static final BigDecimal REORDER_POINT = new BigDecimal("1200.75");

    private StationProductFixture() {
    }

    public static StationProductInputDTO validInput(UUID productId) {
        StationProductInputDTO input = new StationProductInputDTO();
        input.setProductId(productId);
        input.setMinStock(MIN_STOCK);
        input.setReorderPoint(REORDER_POINT);
        input.setActive(true);
        return input;
    }

    public static StationProduct persistedEntity() {
        StationProduct entity = new StationProduct();
        entity.setId(UUID.randomUUID());
        entity.setProductId(UUID.randomUUID());
        entity.setMinStock(MIN_STOCK);
        entity.setReorderPoint(REORDER_POINT);
        entity.setActive(true);
        return entity;
    }
}
