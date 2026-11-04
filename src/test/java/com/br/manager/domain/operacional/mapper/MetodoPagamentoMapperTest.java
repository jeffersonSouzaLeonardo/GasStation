package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.dto.MetodoPagamentoResponseDTO;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.fixture.MetodoPagamentoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MetodoPagamentoMapperTest {

    private final MetodoPagamentoMapper mapper = new MetodoPagamentoMapper();

    @Test
    void shouldMapInputToEntity() {
        MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();
        input.setId(UUID.randomUUID());

        MetodoPagamento metodoPagamento = mapper.pagamentoMethodInputDTOToMetodoPagamento(input);

        assertThat(metodoPagamento)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.pagamentoMethodInputDTOToMetodoPagamento(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        MetodoPagamento metodoPagamento = MetodoPagamentoFixture.persistedEntity();

        MetodoPagamentoResponseDTO response = mapper.pagamentoMethodToMetodoPagamentoResponseDTO(metodoPagamento);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(metodoPagamento);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.pagamentoMethodToMetodoPagamentoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<MetodoPagamento> metodosPagamento = List.of(MetodoPagamentoFixture.persistedEntity(), MetodoPagamentoFixture.persistedEntity());

        List<MetodoPagamentoResponseDTO> responses = mapper.listMetodoPagamentoToListMetodoPagamentoResponseDTO(metodosPagamento);

        assertThat(responses)
                .extracting(MetodoPagamentoResponseDTO::getId)
                .containsExactly(metodosPagamento.get(0).getId(), metodosPagamento.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listMetodoPagamentoToListMetodoPagamentoResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        MetodoPagamento metodoPagamento = MetodoPagamentoFixture.persistedEntity();
        MetodoPagamentoInputDTO input = MetodoPagamentoFixture.outrosInput();
        input.setId(metodoPagamento.getId());

        mapper.updateMetodoPagamentoFromDto(input, metodoPagamento);

        assertThat(metodoPagamento.getNome()).isEqualTo(MetodoPagamentoFixture.OUTROS_NAME);
        assertThat(metodoPagamento.getTipo()).isEqualTo(TipoMetodoPagamentoEnum.DINHEIRO);
        assertThat(metodoPagamento.getExigeReferencia()).isFalse();
        assertThat(metodoPagamento.getDiasLiquidacao()).isZero();
    }

    @Test
    void shouldIgnoreUpdateWhenEntityIsNull() {
        MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();

        mapper.updateMetodoPagamentoFromDto(input, null);

        assertThat(input.getNome()).isEqualTo(MetodoPagamentoFixture.NAME);
    }
}
