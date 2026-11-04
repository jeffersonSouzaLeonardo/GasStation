package com.br.manager.domain.aquisicao_preco.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemPrecoInputDTOTest {

    @Test
    void shouldBeAtivoByDefault() {
        assertThat(new ItemPrecoInputDTO().getAtivo()).isTrue();
    }
}
