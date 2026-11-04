package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReembolsoVendaMapperTest {

    private final ReembolsoVendaMapper mapper = new ReembolsoVendaMapper();

    @Test
    void shouldMapInputToEntityWithoutVenda() {
        VendaReembolsoInputDTO input = VendaReembolsoFixture.input(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        ReembolsoVenda reembolso = mapper.toEntity(input);

        assertThat(reembolso)
                .usingRecursiveComparison()
                .ignoringFields("venda")
                .isEqualTo(input);
        assertThat(reembolso.getVenda()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        ReembolsoVenda reembolso = VendaReembolsoFixture.persistedEntity();

        VendaReembolsoResponseDTO response = mapper.toResponse(reembolso);

        assertThat(response).usingRecursiveComparison().isEqualTo(reembolso);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<ReembolsoVenda> reembolsos = List.of(VendaReembolsoFixture.persistedEntity(), VendaReembolsoFixture.persistedEntity());

        List<VendaReembolsoResponseDTO> responses = mapper.toResponseList(reembolsos);

        assertThat(responses)
                .extracting(VendaReembolsoResponseDTO::id)
                .containsExactly(reembolsos.get(0).getId(), reembolsos.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
