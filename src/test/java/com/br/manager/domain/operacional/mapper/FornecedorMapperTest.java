package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.fixture.FornecedorFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FornecedorMapperTest {

    private final FornecedorMapper mapper = new FornecedorMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        FornecedorInputDTO input = FornecedorFixture.validoInput();
        input.setId(UUID.randomUUID());

        Fornecedor fornecedor = mapper.fornecedorInputDTOToFornecedor(input);

        assertThat(fornecedor.getId()).isEqualTo(input.getId());
        assertThat(fornecedor.getLegalName()).isEqualTo(FornecedorFixture.LEGAL_NAME);
        assertThat(fornecedor.getCnpjCpf()).isEqualTo(FornecedorFixture.CNPJ);
        assertThat(fornecedor.getInscricaoEstadual()).isEqualTo(FornecedorFixture.STATE_REGISTRATION);
        assertThat(fornecedor.getTelefone()).isEqualTo(FornecedorFixture.PHONE);
        assertThat(fornecedor.getCep()).isEqualTo(FornecedorFixture.CEP);
        assertThat(fornecedor.getEmail()).isEqualTo(FornecedorFixture.EMAIL);
        assertThat(fornecedor.getRua()).isEqualTo(FornecedorFixture.RUA);
        assertThat(fornecedor.getNumeroEndereco()).isEqualTo(FornecedorFixture.NUMERO_ENDERECO);
        assertThat(fornecedor.getComplemento()).isEqualTo(FornecedorFixture.COMPLEMENTO);
        assertThat(fornecedor.getBairro()).isEqualTo(FornecedorFixture.BAIRRO);
        assertThat(fornecedor.getCidade()).isEqualTo(FornecedorFixture.CIDADE);
        assertThat(fornecedor.getEstado()).isEqualTo(FornecedorFixture.ESTADO);
        assertThat(fornecedor.getAtivo()).isTrue();
    }

    @Test
    void shouldMapEntityToResponse() {
        Fornecedor fornecedor = FornecedorFixture.persistedEntity();

        FornecedorResponseDTO response = mapper.fornecedorToFornecedorResponseDTO(fornecedor);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("criadoEm", "atualizadoEm", "excluidoEm")
                .isEqualTo(fornecedor);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Fornecedor> fornecedores = List.of(FornecedorFixture.persistedEntity(), FornecedorFixture.persistedEntity());

        List<FornecedorResponseDTO> responses = mapper.listFornecedorToListFornecedorResponseDTO(fornecedores);

        assertThat(responses)
                .extracting(FornecedorResponseDTO::getId)
                .containsExactly(fornecedores.get(0).getId(), fornecedores.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.listFornecedorToListFornecedorResponseDTO(List.of())).isEmpty();
    }

    @Test
    void shouldUpdateEntityFromInputKeepingIdAndRemovingMasks() {
        Fornecedor fornecedor = FornecedorFixture.persistedEntity();
        UUID originalId = fornecedor.getId();
        FornecedorInputDTO input = FornecedorFixture.outrosInput();
        input.setId(UUID.randomUUID());
        input.setAtivo(false);

        mapper.updateFornecedorFromDto(input, fornecedor);

        assertThat(fornecedor.getId()).isEqualTo(originalId);
        assertThat(fornecedor.getLegalName()).isEqualTo(FornecedorFixture.OUTROS_LEGAL_NAME);
        assertThat(fornecedor.getCnpjCpf()).isEqualTo(FornecedorFixture.OUTROS_CNPJ);
        assertThat(fornecedor.getTelefone()).isEqualTo(FornecedorFixture.PHONE);
        assertThat(fornecedor.getCep()).isEqualTo(FornecedorFixture.CEP);
        assertThat(fornecedor.getAtivo()).isFalse();
    }
}
