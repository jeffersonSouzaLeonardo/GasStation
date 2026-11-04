package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import com.br.manager.domain.organizacao.fixture.VinculoUsuarioFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VinculoUsuarioMapperTest {

    private final VinculoUsuarioMapper mapper = new VinculoUsuarioMapper();

    @Test
    void shouldMapInputToEntity() {
        VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
        input.setId(UUID.randomUUID());

        VinculoUsuario usuarioVinculo = mapper.usuarioVinculoInputDTOToVinculoUsuario(input);

        assertThat(usuarioVinculo)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.usuarioVinculoInputDTOToVinculoUsuario(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        VinculoUsuario usuarioVinculo = VinculoUsuarioFixture.persistedEntity();

        VinculoUsuarioResponseDTO response = mapper.usuarioVinculoToVinculoUsuarioResponseDTO(usuarioVinculo);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(usuarioVinculo);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.usuarioVinculoToVinculoUsuarioResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<VinculoUsuario> usuarioLinks = List.of(VinculoUsuarioFixture.persistedEntity(), VinculoUsuarioFixture.persistedEntity());

        List<VinculoUsuarioResponseDTO> responses = mapper.listVinculoUsuarioToListVinculoUsuarioResponseDTO(usuarioLinks);

        assertThat(responses)
                .extracting(VinculoUsuarioResponseDTO::getId)
                .containsExactly(usuarioLinks.get(0).getId(), usuarioLinks.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listVinculoUsuarioToListVinculoUsuarioResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        VinculoUsuario usuarioVinculo = VinculoUsuarioFixture.persistedEntity();
        VinculoUsuarioInputDTO input = VinculoUsuarioFixture.validoInput();
        input.setId(usuarioVinculo.getId());
        input.setCargo(VinculoUsuarioFixture.OUTROS_CARGO);
        input.setAtivo(false);

        mapper.updateVinculoUsuarioFromDto(input, usuarioVinculo);

        assertThat(usuarioVinculo)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        VinculoUsuario usuarioVinculo = VinculoUsuarioFixture.persistedEntity();

        mapper.updateVinculoUsuarioFromDto(null, usuarioVinculo);
        mapper.updateVinculoUsuarioFromDto(VinculoUsuarioFixture.validoInput(), null);

        assertThat(usuarioVinculo.getCargo()).isEqualTo(VinculoUsuarioFixture.CARGO);
    }
}
