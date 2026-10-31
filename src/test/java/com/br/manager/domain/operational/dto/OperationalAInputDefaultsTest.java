package com.br.manager.domain.operational.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Valores padrao dos DTOs de entrada de Produto/Produto do Posto/Tanque/Bomba/Bico.
 */
class OperationalAInputDefaultsTest {

    @Test
    void shouldCreateActiveProductByDefault() {
        assertThat(new ProductInputDTO().getActive()).isTrue();
    }

    @Test
    void shouldCreateActiveStationProductWithZeroStockLevelsByDefault() {
        StationProductInputDTO input = new StationProductInputDTO();

        assertThat(input.getActive()).isTrue();
        assertThat(input.getMinStock()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(input.getReorderPoint()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCreateActiveTankByDefault() {
        assertThat(new TankInputDTO().getActive()).isTrue();
    }

    @Test
    void shouldCreateActivePumpByDefault() {
        assertThat(new PumpInputDTO().getActive()).isTrue();
    }

    @Test
    void shouldCreateActiveNozzleWithZeroMeterByDefault() {
        NozzleInputDTO input = new NozzleInputDTO();

        assertThat(input.getActive()).isTrue();
        assertThat(input.getMeterNumber()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
