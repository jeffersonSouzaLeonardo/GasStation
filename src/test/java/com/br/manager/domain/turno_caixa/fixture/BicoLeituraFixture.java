package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Leitura de Encerrante (bico) do turno.
 */
public final class BicoLeituraFixture {

    public static final BigDecimal MEDIDOR_LEITURA = new BigDecimal("12345.678");
    public static final LocalDateTime READ_EM = LocalDateTime.of(2026, 10, 31, 14, 0);
    public static final String OBSERVACOES = "Leitura conferida pelo supervisor";

    private BicoLeituraFixture() {
    }

    public static BicoLeituraInputDTO validoInput(UUID bicoId, UUID registradoPor, TipoLeituraBicoEnum tipoLeitura) {
        BicoLeituraInputDTO input = new BicoLeituraInputDTO();
        input.setBicoId(bicoId);
        input.setTipoLeitura(tipoLeitura);
        input.setLeituraMedidor(MEDIDOR_LEITURA);
        input.setLidoEm(READ_EM);
        input.setRegistradoPor(registradoPor);
        input.setObservacoes(OBSERVACOES);
        return input;
    }

    public static LeituraBico persistedEntity(Turno turno, UUID bicoId, UUID registradoPor, TipoLeituraBicoEnum tipoLeitura) {
        LeituraBico leitura = new LeituraBico();
        leitura.setId(UUID.randomUUID());
        leitura.setTurno(turno);
        leitura.setBicoId(bicoId);
        leitura.setTipoLeitura(tipoLeitura);
        leitura.setLeituraMedidor(MEDIDOR_LEITURA);
        leitura.setLidoEm(READ_EM);
        leitura.setRegistradoPor(registradoPor);
        leitura.setObservacoes(OBSERVACOES);
        return leitura;
    }
}
