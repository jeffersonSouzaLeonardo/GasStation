package com.br.manager.domain.organizacao.fixture;

import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import com.br.manager.domain.organizacao.enums.TipoCargoEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Vinculo de usuario (usuario x empresa/posto x papel).
 */
public final class VinculoUsuarioFixture {

    public static final TipoCargoEnum CARGO = TipoCargoEnum.GERENTE;
    public static final TipoCargoEnum OUTROS_CARGO = TipoCargoEnum.CAIXA;

    private VinculoUsuarioFixture() {
    }

    public static VinculoUsuarioInputDTO validoInput(UUID usuarioId, UUID empresaId, UUID estacaoId) {
        VinculoUsuarioInputDTO input = new VinculoUsuarioInputDTO();
        input.setUsuarioId(usuarioId);
        input.setEmpresaId(empresaId);
        input.setEstacaoId(estacaoId);
        input.setCargo(CARGO);
        input.setAtivo(true);
        return input;
    }

    public static VinculoUsuarioInputDTO validoInput() {
        return validoInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static VinculoUsuario persistedEntity() {
        VinculoUsuario usuarioVinculo = new VinculoUsuario();
        usuarioVinculo.setId(UUID.randomUUID());
        usuarioVinculo.setUsuarioId(UUID.randomUUID());
        usuarioVinculo.setEmpresaId(UUID.randomUUID());
        usuarioVinculo.setEstacaoId(UUID.randomUUID());
        usuarioVinculo.setCargo(CARGO);
        usuarioVinculo.setAtivo(true);
        return usuarioVinculo;
    }
}
