package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.entity.Abastecimento;
import com.br.manager.domain.operacional.fixture.AbastecimentoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AbastecimentoMapperTest {

    private final AbastecimentoMapper mapper = new AbastecimentoMapper();

    @Test
    void shouldMapRequestToEntityKeepingPdvId() {
        AbastecimentoRequestDTO request = AbastecimentoFixture.validoRequest();

        Abastecimento abastecimento = mapper.abastecimentoRequestDTOToAbastecimento(request);

        assertThat(abastecimento)
                .usingRecursiveComparison()
                .ignoringFields("criadoEm")
                .isEqualTo(request);
        assertThat(abastecimento.getCriadoEm()).isNull();
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        assertThat(mapper.abastecimentoRequestDTOToAbastecimento(null)).isNull();
    }

    @Test
    void shouldMapRequestListToEntityList() {
        List<AbastecimentoRequestDTO> requests = List.of(AbastecimentoFixture.validoRequest(), AbastecimentoFixture.validoRequest());

        List<Abastecimento> abastecimentos = mapper.listAbastecimentoRequestDTOToListAbastecimento(requests);

        assertThat(abastecimentos)
                .extracting(Abastecimento::getId)
                .containsExactly(requests.get(0).getId(), requests.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenRequestListIsNull() {
        assertThat(mapper.listAbastecimentoRequestDTOToListAbastecimento(null)).isNull();
    }
}
