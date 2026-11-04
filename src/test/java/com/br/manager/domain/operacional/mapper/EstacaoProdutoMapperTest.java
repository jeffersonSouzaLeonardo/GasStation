package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.dto.EstacaoProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.EstacaoProduto;
import com.br.manager.domain.operacional.fixture.EstacaoProdutoFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EstacaoProdutoMapperTest {

    private final EstacaoProdutoMapper mapper = new EstacaoProdutoMapper();

    @Test
    void shouldMapInputToEntity() {
        EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(UUID.randomUUID());
        input.setId(UUID.randomUUID());

        EstacaoProduto entity = mapper.estacaoProdutoInputDTOToEstacaoProduto(input);

        assertThat(entity).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.estacaoProdutoInputDTOToEstacaoProduto(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        EstacaoProduto entity = EstacaoProdutoFixture.persistedEntity();

        EstacaoProdutoResponseDTO response = mapper.estacaoProdutoToEstacaoProdutoResponseDTO(entity);

        assertThat(response).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.estacaoProdutoToEstacaoProdutoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<EstacaoProduto> entities = List.of(EstacaoProdutoFixture.persistedEntity(), EstacaoProdutoFixture.persistedEntity());

        List<EstacaoProdutoResponseDTO> responses = mapper.listEstacaoProdutoToListEstacaoProdutoResponseDTO(entities);

        assertThat(responses)
                .extracting(EstacaoProdutoResponseDTO::getId)
                .containsExactly(entities.get(0).getId(), entities.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listEstacaoProdutoToListEstacaoProdutoResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        EstacaoProduto entity = EstacaoProdutoFixture.persistedEntity();
        EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(entity.getProdutoId());
        input.setId(entity.getId());
        input.setEstoqueMinimo(BigDecimal.TEN);

        mapper.updateEstacaoProdutoFromDto(input, entity);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getEstoqueMinimo()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldIgnoreUpdateWhenEntityIsNull() {
        EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(UUID.randomUUID());

        assertThatCode(() -> mapper.updateEstacaoProdutoFromDto(input, null)).doesNotThrowAnyException();
    }
}
