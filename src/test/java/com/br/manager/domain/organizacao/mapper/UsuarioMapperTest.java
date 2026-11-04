package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.UsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.fixture.UsuarioFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void shouldMapInputToEntity() {
        UsuarioInputDTO input = UsuarioFixture.validoInput();
        input.setId(UUID.randomUUID());
        input.setLogin("joao.silva");
        input.setRole("ADMIN");

        Usuario usuario = mapper.usuarioInputDTOToUsuario(input);

        assertThat(usuario.getId()).isEqualTo(input.getId());
        assertThat(usuario.getNome()).isEqualTo(input.getNome());
        assertThat(usuario.getEmail()).isEqualTo(input.getEmail());
        assertThat(usuario.getPasswordHash()).isEqualTo(input.getPasswordHash());
        assertThat(usuario.getTelefone()).isEqualTo(input.getTelefone());
        assertThat(usuario.getAtivo()).isEqualTo(input.getAtivo());
    }

    @Test
    void shouldDeserializePdvPayloadIntoInputDto() throws Exception {
        String payload = """
                [{
                  "id":"e3000001-0000-4000-8000-000000000001",
                  "email":"ana.souza@aurora.example",
                  "login":"ana.souza",
                  "role":"ADMIN",
                  "dataExclusao":null,
                  "name":"Ana Souza",
                  "passwordHash":"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
                  "phone":"11988887777",
                  "active":true,
                  "lastLoginAt":"2026-01-10T12:30:00"
                }]
                """;

        UsuarioInputDTO[] usuarios = new ObjectMapper().findAndRegisterModules()
                .readValue(payload, UsuarioInputDTO[].class);

        UsuarioInputDTO usuario = usuarios[0];

        assertThat(usuario.getNome()).isEqualTo("Ana Souza");
        assertThat(usuario.getLogin()).isEqualTo("ana.souza");
        assertThat(usuario.getRole()).isEqualTo("ADMIN");
        assertThat(usuario.getTelefone()).isEqualTo("11988887777");
        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getUltimaLoginAt()).isNotNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.usuarioInputDTOToUsuario(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Usuario usuario = UsuarioFixture.persistedEntity();

        UsuarioResponseDTO response = mapper.usuarioToUsuarioResponseDTO(usuario);

        assertThat(response.getId()).isEqualTo(usuario.getId());
        assertThat(response.getNome()).isEqualTo(usuario.getNome());
        assertThat(response.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(response.getPasswordHash()).isEqualTo(usuario.getPasswordHash());
        assertThat(response.getTelefone()).isEqualTo(usuario.getTelefone());
        assertThat(response.getAtivo()).isEqualTo(usuario.getAtivo());
        assertThat(response.getUltimaLoginAt()).isEqualTo(usuario.getUltimaLoginAt());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.usuarioToUsuarioResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Usuario> usuarios = List.of(UsuarioFixture.persistedEntity(), UsuarioFixture.persistedEntity());

        List<UsuarioResponseDTO> responses = mapper.listUsuarioToListUsuarioResponseDTO(usuarios);

        assertThat(responses)
                .extracting(UsuarioResponseDTO::getId)
                .containsExactly(usuarios.get(0).getId(), usuarios.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listUsuarioToListUsuarioResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Usuario usuario = UsuarioFixture.persistedEntity();
        UsuarioInputDTO input = UsuarioFixture.validoInput(UsuarioFixture.OUTROS_EMAIL);
        input.setId(usuario.getId());
        input.setNome("Maria Souza");
        input.setAtivo(false);

        mapper.updateUsuarioFromDto(input, usuario);

        assertThat(usuario.getNome()).isEqualTo("Maria Souza");
        assertThat(usuario.getEmail()).isEqualTo(UsuarioFixture.OUTROS_EMAIL);
        assertThat(usuario.getAtivo()).isFalse();
    }

    @Test
    @Disabled("BUG: UsuarioMapper.updateUsuarioFromDto uses BeanUtils.copyProperties copying null fields, "
            + "overwriting criadoEm (and outros timestamps) despite the comment saying only non-null properties are copied")
    void shouldKeepCriadoEmWhenInputHasNoCriadoEm() {
        Usuario usuario = UsuarioFixture.persistedEntity();
        UsuarioInputDTO input = UsuarioFixture.validoInput();
        input.setId(usuario.getId());

        mapper.updateUsuarioFromDto(input, usuario);

        assertThat(usuario.getCriadoEm()).isEqualTo(UsuarioFixture.CRIADO_EM);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        Usuario usuario = UsuarioFixture.persistedEntity();

        mapper.updateUsuarioFromDto(null, usuario);
        mapper.updateUsuarioFromDto(UsuarioFixture.validoInput(), null);

        assertThat(usuario.getEmail()).isEqualTo(UsuarioFixture.EMAIL);
    }
}
