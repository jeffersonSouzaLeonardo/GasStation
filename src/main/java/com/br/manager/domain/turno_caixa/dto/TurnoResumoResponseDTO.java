package com.br.manager.domain.turno_caixa.dto;

import java.math.BigDecimal;
import java.util.List;

public class TurnoResumoResponseDTO {

    private TurnoResponseDTO turno;
    private BigDecimal grossVendas;
    private BigDecimal calibracaoAndTestAjuste;
    private BigDecimal netVendas;
    private BigDecimal fundoAbertura;
    private BigDecimal fechamentoDinheiro;
    private BigDecimal totalDiferencaValor;
    private List<MovimentoCaixaResponseDTO> movimentosCaixa;
    private List<FechamentoPagamentoResponseDTO> fechamentosPagamento;

    public TurnoResponseDTO getTurno() {
        return turno;
    }

    public void setTurno(TurnoResponseDTO turno) {
        this.turno = turno;
    }

    public BigDecimal getGrossVendas() {
        return grossVendas;
    }

    public void setGrossVendas(BigDecimal grossVendas) {
        this.grossVendas = grossVendas;
    }

    public BigDecimal getCalibracaoAndTestAjuste() {
        return calibracaoAndTestAjuste;
    }

    public void setCalibracaoAndTestAjuste(BigDecimal calibracaoAndTestAjuste) {
        this.calibracaoAndTestAjuste = calibracaoAndTestAjuste;
    }

    public BigDecimal getNetVendas() {
        return netVendas;
    }

    public void setNetVendas(BigDecimal netVendas) {
        this.netVendas = netVendas;
    }

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public BigDecimal getFechamentoDinheiro() {
        return fechamentoDinheiro;
    }

    public void setFechamentoDinheiro(BigDecimal fechamentoDinheiro) {
        this.fechamentoDinheiro = fechamentoDinheiro;
    }

    public BigDecimal getTotalDiferencaValor() {
        return totalDiferencaValor;
    }

    public void setTotalDiferencaValor(BigDecimal totalDiferencaValor) {
        this.totalDiferencaValor = totalDiferencaValor;
    }

    public List<MovimentoCaixaResponseDTO> getMovimentosCaixa() {
        return movimentosCaixa;
    }

    public void setMovimentosCaixa(List<MovimentoCaixaResponseDTO> movimentosCaixa) {
        this.movimentosCaixa = movimentosCaixa;
    }

    public List<FechamentoPagamentoResponseDTO> getFechamentosPagamento() {
        return fechamentosPagamento;
    }

    public void setFechamentosPagamento(List<FechamentoPagamentoResponseDTO> fechamentosPagamento) {
        this.fechamentosPagamento = fechamentosPagamento;
    }
}
