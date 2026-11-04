package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Forma de Pagamento.
 */
public final class MetodoPagamentoFixture {

    public static final String NAME = "Cartao de Credito Visa";
    public static final String OUTROS_NAME = "Dinheiro";
    public static final TipoMetodoPagamentoEnum TIPO = TipoMetodoPagamentoEnum.CARTAO_CREDITO;
    public static final int LIQUIDACAO_DIAS = 30;

    private MetodoPagamentoFixture() {
    }

    public static MetodoPagamentoInputDTO validoInput() {
        MetodoPagamentoInputDTO input = new MetodoPagamentoInputDTO();
        input.setNome(NAME);
        input.setTipo(TIPO);
        input.setExigeReferencia(true);
        input.setDiasLiquidacao(LIQUIDACAO_DIAS);
        input.setAtivo(true);
        return input;
    }

    public static MetodoPagamentoInputDTO outrosInput() {
        MetodoPagamentoInputDTO input = new MetodoPagamentoInputDTO();
        input.setNome(OUTROS_NAME);
        input.setTipo(TipoMetodoPagamentoEnum.DINHEIRO);
        return input;
    }

    public static MetodoPagamento persistedEntity() {
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setId(UUID.randomUUID());
        metodoPagamento.setNome(NAME);
        metodoPagamento.setTipo(TIPO);
        metodoPagamento.setExigeReferencia(true);
        metodoPagamento.setDiasLiquidacao(LIQUIDACAO_DIAS);
        metodoPagamento.setAtivo(true);
        return metodoPagamento;
    }
}
