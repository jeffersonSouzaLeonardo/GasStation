package com.br.manager.domain.price_acquisition.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceItemInputDTOTest {

    @Test
    void shouldBeActiveByDefault() {
        assertThat(new PriceItemInputDTO().getActive()).isTrue();
    }
}
