package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Fechamento por Forma de Pagamento do turno.
 */
public final class FechamentoPagamentoFixture {

    public static final BigDecimal ESPERADO_VALOR = new BigDecimal("500.00");
    public static final BigDecimal DECLARADO_VALOR = new BigDecimal("480.00");
    public static final BigDecimal DIFERENCA_VALOR = new BigDecimal("-20.00");
    public static final BigDecimal NEGATIVE_VALOR = new BigDecimal("-1.00");
    public static final String COMPROVANTE_REFERENCIA = "COMPROVANTE-123";

    private FechamentoPagamentoFixture() {
    }

    public static FechamentoPagamentoInputDTO validoInput(UUID pagamentoMethodId) {
        FechamentoPagamentoInputDTO input = new FechamentoPagamentoInputDTO();
        input.setPagamentoMethodId(pagamentoMethodId);
        input.setValorEsperado(ESPERADO_VALOR);
        input.setValorDeclarado(DECLARADO_VALOR);
        input.setReferenciaComprovante(COMPROVANTE_REFERENCIA);
        return input;
    }

    public static FechamentoPagamento persistedEntity(Turno turno, UUID pagamentoMethodId) {
        FechamentoPagamento fechamento = new FechamentoPagamento();
        fechamento.setId(UUID.randomUUID());
        fechamento.setTurno(turno);
        fechamento.setPagamentoMethodId(pagamentoMethodId);
        fechamento.setValorEsperado(ESPERADO_VALOR);
        fechamento.setValorDeclarado(DECLARADO_VALOR);
        fechamento.setValorDiferenca(DIFERENCA_VALOR);
        fechamento.setReferenciaComprovante(COMPROVANTE_REFERENCIA);
        return fechamento;
    }
}
