package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.dto.EstacaoResponseDTO;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.fixture.EstacaoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstacaoMapperTest {

    private final EstacaoMapper mapper = new EstacaoMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        EstacaoInputDTO input = EstacaoFixture.validoInput();
        input.setId(UUID.randomUUID());

        Estacao estacao = mapper.estacaoInputDTOToEstacao(input);

        assertThat(estacao.getId()).isEqualTo(input.getId());
        assertThat(estacao.getEmpresaId()).isEqualTo(input.getEmpresaId());
        assertThat(estacao.getNome()).isEqualTo(EstacaoFixture.NAME);
        assertThat(estacao.getCnpj()).isEqualTo(EstacaoFixture.CNPJ);
        assertThat(estacao.getInscricaoEstadual()).isEqualTo(EstacaoFixture.STATE_REGISTRATION);
        assertThat(estacao.getCep()).isEqualTo(EstacaoFixture.CEP);
        assertThat(estacao.getRua()).isEqualTo(EstacaoFixture.RUA);
        assertThat(estacao.getNumeroEndereco()).isEqualTo(EstacaoFixture.NUMERO);
        assertThat(estacao.getComplemento()).isEqualTo(EstacaoFixture.COMPLEMENT);
        assertThat(estacao.getBairro()).isEqualTo(EstacaoFixture.BAIRRO);
        assertThat(estacao.getCidade()).isEqualTo(EstacaoFixture.CITY);
        assertThat(estacao.getEstado()).isEqualTo(EstacaoFixture.STATE);
        assertThat(estacao.getFusoHorario()).isEqualTo(EstacaoFixture.FusoHorario);
        assertThat(estacao.getAtivo()).isTrue();
    }

    @Test
    void shouldMapNullMaskedFieldsToEmptyText() {
        EstacaoInputDTO input = EstacaoFixture.validoInput();
        input.setCnpj(null);
        input.setInscricaoEstadual(null);
        input.setCep(null);

        Estacao estacao = mapper.estacaoInputDTOToEstacao(input);

        assertThat(estacao.getCnpj()).isEmpty();
        assertThat(estacao.getInscricaoEstadual()).isEmpty();
        assertThat(estacao.getCep()).isEmpty();
    }

    @Test
    void shouldMapEntityToResponse() {
        Estacao estacao = EstacaoFixture.persistedEntity();

        EstacaoResponseDTO response = mapper.estacaoToEstacaoResponseDTO(estacao);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(estacao);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Estacao> estacoes = List.of(EstacaoFixture.persistedEntity(), EstacaoFixture.persistedEntity());

        List<EstacaoResponseDTO> responses = mapper.listEstacaoToListEstacaoResponseDTO(estacoes);

        assertThat(responses)
                .extracting(EstacaoResponseDTO::getId)
                .containsExactly(estacoes.get(0).getId(), estacoes.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.listEstacaoToListEstacaoResponseDTO(List.of())).isEmpty();
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        Estacao estacao = EstacaoFixture.persistedEntity();
        UUID originalId = estacao.getId();
        UUID newEmpresaId = UUID.randomUUID();
        EstacaoInputDTO input = EstacaoFixture.validoInput(newEmpresaId);
        input.setId(UUID.randomUUID());
        input.setNome("Posto Novo");
        input.setFusoHorario(EstacaoFixture.OUTROS_TIMEZONE);
        input.setAtivo(false);

        mapper.updateEstacaoFromDto(input, estacao);

        assertThat(estacao.getId()).isEqualTo(originalId);
        assertThat(estacao.getEmpresaId()).isEqualTo(newEmpresaId);
        assertThat(estacao.getNome()).isEqualTo("Posto Novo");
        assertThat(estacao.getCnpj()).isEqualTo(EstacaoFixture.CNPJ);
        assertThat(estacao.getCep()).isEqualTo(EstacaoFixture.CEP);
        assertThat(estacao.getFusoHorario()).isEqualTo(EstacaoFixture.OUTROS_TIMEZONE);
        assertThat(estacao.getAtivo()).isFalse();
    }
}
