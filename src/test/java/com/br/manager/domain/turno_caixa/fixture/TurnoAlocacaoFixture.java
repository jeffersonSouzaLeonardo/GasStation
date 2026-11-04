package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.enums.CargoAlocacaoTurnoEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Alocacao de Turno.
 */
public final class TurnoAlocacaoFixture {

    public static final CargoAlocacaoTurnoEnum CARGO = CargoAlocacaoTurnoEnum.CAIXA;
    public static final CargoAlocacaoTurnoEnum OUTROS_CARGO = CargoAlocacaoTurnoEnum.FRENTISTA;

    private TurnoAlocacaoFixture() {
    }

    public static TurnoAlocacaoInputDTO validoInput(UUID usuarioId) {
        return input(usuarioId, CARGO);
    }

    public static TurnoAlocacaoInputDTO validoInput() {
        return validoInput(UUID.randomUUID());
    }

    public static TurnoAlocacaoInputDTO input(UUID usuarioId, CargoAlocacaoTurnoEnum cargo) {
        TurnoAlocacaoInputDTO input = new TurnoAlocacaoInputDTO();
        input.setUsuarioId(usuarioId);
        input.setCargo(cargo);
        return input;
    }

    public static AlocacaoTurno persistedEntity(Turno turno, UUID usuarioId) {
        AlocacaoTurno alocacao = new AlocacaoTurno();
        alocacao.setId(UUID.randomUUID());
        alocacao.setTurno(turno);
        alocacao.setUsuarioId(usuarioId);
        alocacao.setCargo(CARGO);
        return alocacao;
    }
}
