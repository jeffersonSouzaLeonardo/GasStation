package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.entity.Combustivel;
import com.br.manager.domain.operacional.enums.SituacaoCombustivelEnum;
import com.br.manager.domain.operacional.enums.UnidadeCombustivelEnum;

/**
 * Massa de dados padrao para cenarios de Combustivel.
 */
public final class CombustivelFixture {

    public static final Long ID = 10L;
    public static final String NAME = "Gasolina Comum";
    public static final String OUTROS_NAME = "Etanol Hidratado";
    public static final UnidadeCombustivelEnum UNIDADE = UnidadeCombustivelEnum.LITROS;
    public static final SituacaoCombustivelEnum STATUS = SituacaoCombustivelEnum.ATIVO;
    public static final String ID_ANP = "320102001";

    private CombustivelFixture() {
    }

    public static CombustivelInputDTO validoInput() {
        CombustivelInputDTO input = new CombustivelInputDTO();
        input.setNome(NAME);
        input.setUnidade(UNIDADE);
        input.setStatus(STATUS);
        input.setIdAnp(ID_ANP);
        return input;
    }

    public static CombustivelInputDTO outrosInput() {
        CombustivelInputDTO input = validoInput();
        input.setNome(OUTROS_NAME);
        return input;
    }

    public static Combustivel persistedEntity() {
        Combustivel combustivel = new Combustivel();
        combustivel.setId(ID);
        combustivel.setNome(NAME);
        combustivel.setUnidade(UNIDADE);
        combustivel.setStatus(STATUS);
        combustivel.setIdAnp(ID_ANP);
        return combustivel;
    }
}
