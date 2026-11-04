package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.fixture.BombaFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BombaMapperTest {

    private final BombaMapper mapper = new BombaMapper();

    @Test
    void shouldMapInputToEntity() {
        BombaInputDTO input = BombaFixture.validoInput();
        input.setId(UUID.randomUUID());

        Bomba bomba = mapper.bombaInputDTOToBomba(input);

        assertThat(bomba)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "codigo", "localizacao", "ativo")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.bombaInputDTOToBomba(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Bomba bomba = BombaFixture.persistedEntity();

        BombaResponseDTO response = mapper.bombaToBombaResponseDTO(bomba);

        assertThat(response).usingRecursiveComparison().isEqualTo(bomba);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.bombaToBombaResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Bomba> bombas = List.of(BombaFixture.persistedEntity(), BombaFixture.persistedEntity());

        List<BombaResponseDTO> responses = mapper.listBombaToListBombaResponseDTO(bombas);

        assertThat(responses)
                .extracting(BombaResponseDTO::getId)
                .containsExactly(bombas.get(0).getId(), bombas.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listBombaToListBombaResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Bomba bomba = BombaFixture.persistedEntity();
        BombaInputDTO input = BombaFixture.validoInput(BombaFixture.OUTROS_CODE);
        input.setId(bomba.getId());

        mapper.updateBombaFromDto(input, bomba);

        assertThat(bomba.getId()).isEqualTo(input.getId());
        assertThat(bomba.getCodigo()).isEqualTo(BombaFixture.OUTROS_CODE);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Bomba bomba = BombaFixture.persistedEntity();

        mapper.updateBombaFromDto(null, bomba);

        assertThat(bomba.getCodigo()).isEqualTo(BombaFixture.CODE);
    }
}
