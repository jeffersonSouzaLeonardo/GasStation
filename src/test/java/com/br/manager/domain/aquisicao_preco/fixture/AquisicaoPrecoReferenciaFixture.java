package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;

import java.util.UUID;

/**
 * Entidades de outros dominios (usuario, fornecedor) usadas como referencia nos testes unitarios.
 */
public final class AquisicaoPrecoReferenciaFixture {

    public static final String USUARIO_NAME = "Comprador Teste";
    public static final String FORNECEDOR_NAME = "Distribuidora Teste SA";

    private AquisicaoPrecoReferenciaFixture() {
    }

    public static Usuario ativoUsuario() {
        return usuario(true);
    }

    public static Usuario inativoUsuario() {
        return usuario(false);
    }

    public static Fornecedor ativoFornecedor() {
        return fornecedor(true);
    }

    public static Fornecedor inativoFornecedor() {
        return fornecedor(false);
    }

    private static Usuario usuario(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome(USUARIO_NAME);
        usuario.setEmail("comprador@posto.com.br");
        usuario.setPasswordHash("hash");
        usuario.setAtivo(ativo);
        return usuario;
    }

    private static Fornecedor fornecedor(boolean ativo) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(UUID.randomUUID());
        fornecedor.setLegalName(FORNECEDOR_NAME);
        fornecedor.setAtivo(ativo);
        return fornecedor;
    }
}
