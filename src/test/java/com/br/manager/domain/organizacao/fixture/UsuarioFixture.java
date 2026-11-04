package com.br.manager.domain.organizacao.fixture;

import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.entity.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Usuario.
 */
public final class UsuarioFixture {

    public static final String NAME = "Joao da Silva";
    public static final String EMAIL = "joao.silva@postobom.com.br";
    public static final String OUTROS_EMAIL = "maria.souza@postobom.com.br";
    public static final String PASSWORD_HASH = "$2a$10$hashDeSenhaFicticio";
    public static final String PHONE = "11987654321";
    public static final LocalDateTime CRIADO_EM = LocalDateTime.of(2026, 3, 10, 8, 0);

    private UsuarioFixture() {
    }

    public static UsuarioInputDTO validoInput() {
        return validoInput(EMAIL);
    }

    public static UsuarioInputDTO validoInput(String email) {
        UsuarioInputDTO input = new UsuarioInputDTO();
        input.setNome(NAME);
        input.setEmail(email);
        input.setPasswordHash(PASSWORD_HASH);
        input.setTelefone(PHONE);
        input.setAtivo(true);
        return input;
    }

    public static Usuario persistedEntity() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome(NAME);
        usuario.setEmail(EMAIL);
        usuario.setPasswordHash(PASSWORD_HASH);
        usuario.setTelefone(PHONE);
        usuario.setAtivo(true);
        usuario.setCriadoEm(CRIADO_EM);
        return usuario;
    }
}
