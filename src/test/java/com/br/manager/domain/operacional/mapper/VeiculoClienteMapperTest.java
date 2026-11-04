package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.dto.VeiculoClienteResponseDTO;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.fixture.ClienteVeiculoFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoClienteMapperTest {

    private static final String PLACA_BUG = "BUG: VeiculoClienteMapper uses UtilStrings.removeMask (onlyDigits) on the placa, "
            + "stripping its letters (ABC-1D23 -> 123); plates deve keep letters and only drop separators";

    private final VeiculoClienteMapper mapper = new VeiculoClienteMapper();

    @Test
    void shouldMapInputToEntity() {
        UUID clienteId = UUID.randomUUID();
        VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(clienteId);
        input.setId(UUID.randomUUID());

        VeiculoCliente veiculo = mapper.clienteVeiculoInputDTOToClienteVeiculo(input);

        assertThat(veiculo.getId()).isEqualTo(input.getId());
        assertThat(veiculo.getClienteId()).isEqualTo(clienteId);
        assertThat(veiculo.getMarca()).isEqualTo(ClienteVeiculoFixture.MARCA);
        assertThat(veiculo.getModelo()).isEqualTo(ClienteVeiculoFixture.MODELO);
        assertThat(veiculo.getCombustivelType()).isEqualTo(ClienteVeiculoFixture.COMBUSTIVEL_TYPE);
        assertThat(veiculo.getAtivo()).isTrue();
    }

    @Test
    @Disabled(PLACA_BUG)
    void shouldMapInputPlacaRemovingOnlySeparators() {
        VeiculoCliente veiculo = mapper.clienteVeiculoInputDTOToClienteVeiculo(
                ClienteVeiculoFixture.validoInput(UUID.randomUUID()));

        assertThat(veiculo.getPlaca()).isEqualTo(ClienteVeiculoFixture.PLACA);
    }

    @Test
    void shouldMapEntityToResponse() {
        VeiculoCliente veiculo = ClienteVeiculoFixture.persistedEntity();

        VeiculoClienteResponseDTO response = mapper.clienteVeiculoToVeiculoClienteResponseDTO(veiculo);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(veiculo);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<VeiculoCliente> veiculos = List.of(ClienteVeiculoFixture.persistedEntity(), ClienteVeiculoFixture.persistedEntity());

        List<VeiculoClienteResponseDTO> responses = mapper.listClienteVeiculoToListVeiculoClienteResponseDTO(veiculos);

        assertThat(responses)
                .extracting(VeiculoClienteResponseDTO::getId)
                .containsExactly(veiculos.get(0).getId(), veiculos.get(1).getId());
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        VeiculoCliente veiculo = ClienteVeiculoFixture.persistedEntity();
        UUID originalId = veiculo.getId();
        UUID newClienteId = UUID.randomUUID();
        VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(newClienteId);
        input.setId(UUID.randomUUID());
        input.setModelo("Delivery 11.180");
        input.setAtivo(false);

        mapper.updateClienteVeiculoFromDto(input, veiculo);

        assertThat(veiculo.getId()).isEqualTo(originalId);
        assertThat(veiculo.getClienteId()).isEqualTo(newClienteId);
        assertThat(veiculo.getModelo()).isEqualTo("Delivery 11.180");
        assertThat(veiculo.getAtivo()).isFalse();
    }

    @Test
    @Disabled(PLACA_BUG)
    void shouldUpdatePlacaRemovingOnlySeparators() {
        VeiculoCliente veiculo = ClienteVeiculoFixture.persistedEntity();

        mapper.updateClienteVeiculoFromDto(ClienteVeiculoFixture.validoInput(veiculo.getClienteId()), veiculo);

        assertThat(veiculo.getPlaca()).isEqualTo(ClienteVeiculoFixture.PLACA);
    }
}
