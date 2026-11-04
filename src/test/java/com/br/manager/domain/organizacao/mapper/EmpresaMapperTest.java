package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.dto.EmpresaResponseDTO;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.fixture.EmpresaFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmpresaMapperTest {

    private final EmpresaMapper mapper = new EmpresaMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        EmpresaInputDTO input = EmpresaFixture.validoInput();
        input.setId(UUID.randomUUID());

        Empresa empresa = mapper.empresaInputDTOToEmpresa(input);

        assertThat(empresa.getId()).isEqualTo(input.getId());
        assertThat(empresa.getLegalName()).isEqualTo(EmpresaFixture.LEGAL_NAME);
        assertThat(empresa.getTradeName()).isEqualTo(EmpresaFixture.TRADE_NAME);
        assertThat(empresa.getCnpj()).isEqualTo(EmpresaFixture.CNPJ);
        assertThat(empresa.getInscricaoEstadual()).isEqualTo("123456789");
        assertThat(empresa.getTelefone()).isEqualTo(EmpresaFixture.PHONE);
        assertThat(empresa.getTaxRegime()).isEqualTo(input.getTaxRegime());
        assertThat(empresa.getEmail()).isEqualTo(input.getEmail());
        assertThat(empresa.getAtivo()).isTrue();
    }

    @Test
    void shouldMapEntityToResponse() {
        Empresa empresa = EmpresaFixture.persistedEntity();

        EmpresaResponseDTO response = mapper.empresaToEmpresaResponseDTO(empresa);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(empresa);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Empresa> empresas = List.of(EmpresaFixture.persistedEntity(), EmpresaFixture.persistedEntity());

        List<EmpresaResponseDTO> responses = mapper.listEmpresaToListEmpresaResponseDTO(empresas);

        assertThat(responses)
                .extracting(EmpresaResponseDTO::getId)
                .containsExactly(empresas.get(0).getId(), empresas.get(1).getId());
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        Empresa empresa = EmpresaFixture.persistedEntity();
        UUID originalId = empresa.getId();
        EmpresaInputDTO input = EmpresaFixture.validoInput("98.765.432/0001-10");
        input.setLegalName("Nova Razao");
        input.setAtivo(false);

        mapper.updateEmpresaFromDto(input, empresa);

        assertThat(empresa.getId()).isEqualTo(originalId);
        assertThat(empresa.getLegalName()).isEqualTo("Nova Razao");
        assertThat(empresa.getCnpj()).isEqualTo("98765432000110");
        assertThat(empresa.getAtivo()).isFalse();
    }
}
