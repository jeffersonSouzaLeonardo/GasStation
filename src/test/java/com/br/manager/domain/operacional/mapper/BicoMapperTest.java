package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.fixture.BicoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BicoMapperTest {

    private final BicoMapper mapper = new BicoMapper();

    @Test
    void shouldMapInputToEntity() {
        BicoInputDTO input = BicoFixture.validoInput();
        input.setId(UUID.randomUUID());

        Bico bico = mapper.bicoInputDTOToBico(input);

        assertThat(bico)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "bombaId", "tanqueId", "produtoId", "codigo", "numeroMedidor", "ativo")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.bicoInputDTOToBico(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Bico bico = BicoFixture.persistedEntity();

        BicoResponseDTO response = mapper.bicoToBicoResponseDTO(bico);

        assertThat(response).usingRecursiveComparison().isEqualTo(bico);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.bicoToBicoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Bico> bicos = List.of(BicoFixture.persistedEntity(), BicoFixture.persistedEntity());

        List<BicoResponseDTO> responses = mapper.listBicoToListBicoResponseDTO(bicos);

        assertThat(responses)
                .extracting(BicoResponseDTO::getId)
                .containsExactly(bicos.get(0).getId(), bicos.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listBicoToListBicoResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Bico bico = BicoFixture.persistedEntity();
        BicoInputDTO input = BicoFixture.validoInput();
        input.setId(bico.getId());
        input.setCodigo(BicoFixture.OUTROS_CODE);

        mapper.updateBicoFromDto(input, bico);

        assertThat(bico.getId()).isEqualTo(input.getId());
        assertThat(bico.getCodigo()).isEqualTo(BicoFixture.OUTROS_CODE);
        assertThat(bico.getBombaId()).isEqualTo(input.getBombaId());
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Bico bico = BicoFixture.persistedEntity();

        mapper.updateBicoFromDto(null, bico);

        assertThat(bico.getCodigo()).isEqualTo(BicoFixture.CODE);
    }
}
