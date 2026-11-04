package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BicoLeituraInputDTO {

    @NotNull(message = "O ID de Bico é obrigatório.")
    private UUID bicoId;

    @NotNull(message = "Leitura type é obrigatório.")
    private TipoLeituraBicoEnum tipoLeitura;

    @NotNull(message = "Medidor leitura é obrigatório.")
    @DecimalMin(value = "0.000", message = "Medidor leitura cannot be negative.")
    private BigDecimal leituraMedidor;

    @NotNull(message = "Read date é obrigatório.")
    private LocalDateTime lidoEm;

    @NotNull(message = "Registrado by é obrigatório.")
    private UUID registradoPor;

    private String observacoes;

    public UUID getBicoId() {
        return bicoId;
    }

    public void setBicoId(UUID bicoId) {
        this.bicoId = bicoId;
    }

    public TipoLeituraBicoEnum getTipoLeitura() {
        return tipoLeitura;
    }

    public void setTipoLeitura(TipoLeituraBicoEnum tipoLeitura) {
        this.tipoLeitura = tipoLeitura;
    }

    public BigDecimal getLeituraMedidor() {
        return leituraMedidor;
    }

    public void setLeituraMedidor(BigDecimal leituraMedidor) {
        this.leituraMedidor = leituraMedidor;
    }

    public LocalDateTime getLidoEm() {
        return lidoEm;
    }

    public void setLidoEm(LocalDateTime lidoEm) {
        this.lidoEm = lidoEm;
    }

    public UUID getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UUID registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
