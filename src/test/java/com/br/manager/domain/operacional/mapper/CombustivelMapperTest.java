package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.dto.CombustivelResponseDTO;
import com.br.manager.domain.operacional.entity.Combustivel;
import com.br.manager.domain.operacional.enums.SituacaoCombustivelEnum;
import com.br.manager.domain.operacional.fixture.CombustivelFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CombustivelMapperTest {

    private final CombustivelMapper mapper = new CombustivelMapper();

    @Test
    void shouldMapInputToEntity() {
        CombustivelInputDTO input = CombustivelFixture.validoInput();
        input.setId(CombustivelFixture.ID);

        Combustivel combustivel = mapper.combustivelInputToCombustivelEntity(input);

        assertThat(combustivel)
                .usingRecursiveComparison()
                .ignoringFields("excluido")
                .isEqualTo(input);
        assertThat(combustivel.getExcluido()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.combustivelInputToCombustivelEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Combustivel combustivel = CombustivelFixture.persistedEntity();

        CombustivelResponseDTO response = mapper.combustivelEntityToCombustivelResponseDTO(combustivel);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(combustivel);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.combustivelEntityToCombustivelResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Combustivel> combustiveis = List.of(CombustivelFixture.persistedEntity());

        List<CombustivelResponseDTO> responses = mapper.listCombustivelEntityToListCombustivelResponseDTO(combustiveis);

        assertThat(responses)
                .extracting(CombustivelResponseDTO::getNome)
                .containsExactly(CombustivelFixture.NAME);
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listCombustivelEntityToListCombustivelResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Combustivel combustivel = CombustivelFixture.persistedEntity();
        CombustivelInputDTO input = CombustivelFixture.outrosInput();
        input.setId(combustivel.getId());
        input.setStatus(SituacaoCombustivelEnum.INATIVO);

        mapper.updateCombustivelFromDto(input, combustivel);

        assertThat(combustivel.getNome()).isEqualTo(CombustivelFixture.OUTROS_NAME);
        assertThat(combustivel.getStatus()).isEqualTo(SituacaoCombustivelEnum.INATIVO);
        assertThat(combustivel.getId()).isEqualTo(CombustivelFixture.ID);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Combustivel combustivel = CombustivelFixture.persistedEntity();

        mapper.updateCombustivelFromDto(null, combustivel);

        assertThat(combustivel.getNome()).isEqualTo(CombustivelFixture.NAME);
    }
}
