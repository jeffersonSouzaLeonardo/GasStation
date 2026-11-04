package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MovimentoCaixaInputDTO {

    @NotNull(message = "Movimento type é obrigatório.")
    private TipoMovimentoCaixaEnum tipoMovimento;

    private UUID pagamentoMethodId;

    @NotNull(message = "Valor é obrigatório.")
    @DecimalMin(value = "0.00", message = "Valor cannot be negative.")
    private BigDecimal valor;

    @NotNull(message = "Movimento date é obrigatório.")
    private LocalDateTime ocorridoEm;

    private String referencia;

    private String motivo;

    @NotNull(message = "Criado by é obrigatório.")
    private UUID criadoPor;

    private UUID aprovadoPor;

    public TipoMovimentoCaixaEnum getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoCaixaEnum tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public UUID getPagamentoMethodId() {
        return pagamentoMethodId;
    }

    public void setPagamentoMethodId(UUID pagamentoMethodId) {
        this.pagamentoMethodId = pagamentoMethodId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }

    public void setOcorridoEm(LocalDateTime ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(UUID criadoPor) {
        this.criadoPor = criadoPor;
    }

    public UUID getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(UUID aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }
}
