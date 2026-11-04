package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessaoCaixaResponseDTO {

    private UUID id;
    private UUID turnoId;
    private UUID caixeiroUsuarioId;
    private CargoCaixaEnum cargo;
    private BigDecimal fundoAbertura;
    private SessaoCaixaSituacaoEnum status;
    private LocalDateTime openedAt;
    private LocalDateTime fechadoAt;
    private BigDecimal totalDinheiro;
    private BigDecimal totalCartao;
    private BigDecimal totalPix;
    private BigDecimal totalSangria;
    private String observacoes;
    private LocalDateTime criadoEm;

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

    public UUID getCaixeiroUsuarioId() {
        return caixeiroUsuarioId;
    }

    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) {
        this.caixeiroUsuarioId = caixeiroUsuarioId;
    }

    public CargoCaixaEnum getCargo() {
        return cargo;
    }

    public void setCargo(CargoCaixaEnum cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public SessaoCaixaSituacaoEnum getStatus() {
        return status;
    }

    public void setStatus(SessaoCaixaSituacaoEnum status) {
        this.status = status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getFechadoAt() {
        return fechadoAt;
    }

    public void setFechadoAt(LocalDateTime fechadoAt) {
        this.fechadoAt = fechadoAt;
    }

    public BigDecimal getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(BigDecimal totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public BigDecimal getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(BigDecimal totalCartao) {
        this.totalCartao = totalCartao;
    }

    public BigDecimal getTotalPix() {
        return totalPix;
    }

    public void setTotalPix(BigDecimal totalPix) {
        this.totalPix = totalPix;
    }

    public BigDecimal getTotalSangria() {
        return totalSangria;
    }

    public void setTotalSangria(BigDecimal totalSangria) {
        this.totalSangria = totalSangria;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
