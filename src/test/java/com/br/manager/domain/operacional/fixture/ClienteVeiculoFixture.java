package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.entity.VeiculoCliente;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Veiculo do Cliente.
 */
public final class ClienteVeiculoFixture {

    public static final String MASKED_PLACA = "ABC-1D23";
    public static final String PLACA = "ABC1D23";
    public static final String OUTROS_PLACA = "XYZ9K87";
    public static final String MARCA = "Volkswagen";
    public static final String MODELO = "Constellation 24.280";
    public static final String COMBUSTIVEL_TYPE = "DIESEL";

    private ClienteVeiculoFixture() {
    }

    public static VeiculoClienteInputDTO validoInput(UUID clienteId) {
        VeiculoClienteInputDTO input = new VeiculoClienteInputDTO();
        input.setClienteId(clienteId);
        input.setPlaca(MASKED_PLACA);
        input.setMarca(MARCA);
        input.setModelo(MODELO);
        input.setCombustivelType(COMBUSTIVEL_TYPE);
        input.setAtivo(true);
        return input;
    }

    public static VeiculoCliente persistedEntity() {
        VeiculoCliente veiculo = new VeiculoCliente();
        veiculo.setId(UUID.randomUUID());
        veiculo.setClienteId(UUID.randomUUID());
        veiculo.setPlaca(PLACA);
        veiculo.setMarca(MARCA);
        veiculo.setModelo(MODELO);
        veiculo.setCombustivelType(COMBUSTIVEL_TYPE);
        veiculo.setAtivo(true);
        return veiculo;
    }
}
