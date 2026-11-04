package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BicoLeituraResponseDTO {

    private UUID id;
    private UUID turnoId;
    private UUID bicoId;
    private TipoLeituraBicoEnum tipoLeitura;
    private BigDecimal leituraMedidor;
    private LocalDateTime lidoEm;
    private UUID registradoPor;
    private String observacoes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(UUID turnoId) {
        this.turnoId = turnoId;
    }

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
