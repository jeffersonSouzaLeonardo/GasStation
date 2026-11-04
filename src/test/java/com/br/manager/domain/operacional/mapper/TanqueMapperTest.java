package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.fixture.TanqueFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TanqueMapperTest {

    private final TanqueMapper mapper = new TanqueMapper();

    @Test
    void shouldMapInputToEntity() {
        TanqueInputDTO input = TanqueFixture.validoInput(UUID.randomUUID());
        input.setId(UUID.randomUUID());

        Tanque tanque = mapper.tanqueInputToTanqueEntity(input);

        assertThat(tanque)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "codigo", "produtoId", "capacidadeLitros", "estoqueMortoLitros", "saldoContabilLitros", "ativo")
                .isEqualTo(input);
    }

    @Test
    void shouldDefaultAtivoToTrueWhenInputAtivoIsNull() {
        TanqueInputDTO input = TanqueFixture.validoInput(UUID.randomUUID());
        input.setAtivo(null);

        Tanque tanque = mapper.tanqueInputToTanqueEntity(input);

        assertThat(tanque.getAtivo()).isTrue();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.tanqueInputToTanqueEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Tanque tanque = TanqueFixture.persistedEntity();

        TanqueResponseDTO response = mapper.tanqueEntityToTanqueResponseDTO(tanque);

        assertThat(response).usingRecursiveComparison().isEqualTo(tanque);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.tanqueEntityToTanqueResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Tanque> tanques = List.of(TanqueFixture.persistedEntity(), TanqueFixture.persistedEntity());

        List<TanqueResponseDTO> responses = mapper.listTanqueEntityToListTanqueResponseDTO(tanques);

        assertThat(responses)
                .extracting(TanqueResponseDTO::getId)
                .containsExactly(tanques.get(0).getId(), tanques.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listTanqueEntityToListTanqueResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateOnlyNonNullFields() {
        Tanque tanque = TanqueFixture.persistedEntity();
        TanqueInputDTO input = new TanqueInputDTO();
        input.setCodigo(TanqueFixture.OUTROS_CODE);
        input.setCapacidadeLitros(BigDecimal.TEN);
        input.setAtivo(false);

        mapper.updateTanqueFromDto(input, tanque);

        assertThat(tanque.getCodigo()).isEqualTo(TanqueFixture.OUTROS_CODE);
        assertThat(tanque.getCapacidadeLitros()).isEqualTo(BigDecimal.TEN);
        assertThat(tanque.getAtivo()).isFalse();
        assertThat(tanque.getProdutoId()).isNotNull();
        assertThat(tanque.getEstoqueMortoLitros()).isEqualTo(TanqueFixture.MORTO_ESTOQUE);
        assertThat(tanque.getSaldoContabilLitros()).isEqualTo(TanqueFixture.CURRENT_CONTABIL);
    }

    @Test
    void shouldUpdateAllFieldsWhenAllAreInformed() {
        Tanque tanque = TanqueFixture.persistedEntity();
        UUID originalId = tanque.getId();
        TanqueInputDTO input = TanqueFixture.validoInput(UUID.randomUUID(), TanqueFixture.OUTROS_CODE);
        input.setEstoqueMortoLitros(BigDecimal.ONE);
        input.setSaldoContabilLitros(BigDecimal.TEN);

        mapper.updateTanqueFromDto(input, tanque);

        assertThat(tanque.getId()).isEqualTo(originalId);
        assertThat(tanque.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(tanque.getEstoqueMortoLitros()).isEqualTo(BigDecimal.ONE);
        assertThat(tanque.getSaldoContabilLitros()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Tanque tanque = TanqueFixture.persistedEntity();

        mapper.updateTanqueFromDto(null, tanque);

        assertThat(tanque.getCodigo()).isEqualTo(TanqueFixture.CODE);
    }
}
