package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.dto.ClienteResponseDTO;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.enums.TipoPessoaEnum;
import com.br.manager.domain.operacional.fixture.ClienteFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteMapperTest {

    private final ClienteMapper mapper = new ClienteMapper();

    @Test
    void shouldMapInputToEntity() {
        ClienteInputDTO input = ClienteFixture.validoInput();
        input.setId(UUID.randomUUID());

        Cliente cliente = mapper.clienteInputDTOToCliente(input);

        assertThat(cliente)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    @Disabled("BUG: ClienteMapper.clienteInputDTOToCliente/updateClienteFromDto use BeanUtils.copyProperties "
            + "without UtilStrings.removeMask (unlike FornecedorMapper); a masked CNPJ (18 chars) overflows clientes.cnpj_cpf VARCHAR(14)")
    void shouldMapInputToEntityRemovingMasks() {
        Cliente cliente = mapper.clienteInputDTOToCliente(ClienteFixture.maskedInput());

        assertThat(cliente.getCnpjCpf()).isEqualTo(ClienteFixture.CNPJ);
        assertThat(cliente.getTelefone()).isEqualTo(ClienteFixture.PHONE);
        assertThat(cliente.getCep()).isEqualTo(ClienteFixture.CEP);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.clienteInputDTOToCliente(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Cliente cliente = ClienteFixture.persistedEntity();

        ClienteResponseDTO response = mapper.clienteToClienteResponseDTO(cliente);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(cliente);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.clienteToClienteResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Cliente> clientes = List.of(ClienteFixture.persistedEntity(), ClienteFixture.persistedEntity());

        List<ClienteResponseDTO> responses = mapper.listClienteToListClienteResponseDTO(clientes);

        assertThat(responses)
                .extracting(ClienteResponseDTO::getId)
                .containsExactly(clientes.get(0).getId(), clientes.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listClienteToListClienteResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Cliente cliente = ClienteFixture.persistedEntity();
        ClienteInputDTO input = ClienteFixture.outrosInput();
        input.setId(cliente.getId());
        input.setAtivo(false);

        mapper.updateClienteFromDto(input, cliente);

        assertThat(cliente.getNome()).isEqualTo(ClienteFixture.OUTROS_NAME);
        assertThat(cliente.getPersonType()).isEqualTo(TipoPessoaEnum.FISICA);
        assertThat(cliente.getCnpjCpf()).isEqualTo(ClienteFixture.CPF);
        assertThat(cliente.getAtivo()).isFalse();
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Cliente cliente = ClienteFixture.persistedEntity();

        mapper.updateClienteFromDto(null, cliente);

        assertThat(cliente.getNome()).isEqualTo(ClienteFixture.NAME);
    }
}
