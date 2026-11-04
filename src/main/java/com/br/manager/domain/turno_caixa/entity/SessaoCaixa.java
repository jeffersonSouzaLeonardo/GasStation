package com.br.manager.domain.turno_caixa.entity;

import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sessão de caixa do operador (caixa do PDV central ou frentista), atrelada a um turno.
 * O identificador é gerado pelo PDV e replicado aqui (sem geração automática).
 */
@Entity
@Table(name = "sessoes_caixa")
public class SessaoCaixa {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    @Column(name = "caixeiro_usuario_id", nullable = false)
    private UUID caixeiroUsuarioId;

    @NotNull(message = "O papel do operador é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false, length = 30)
    private CargoCaixaEnum cargo;

    @NotNull(message = "O fundo de troco é obrigatório.")
    @DecimalMin(value = "0.00", message = "O fundo de troco não pode ser negativo.")
    @Column(name = "fundo_abertura", nullable = false, precision = 15, scale = 2)
    private BigDecimal fundoAbertura = BigDecimal.ZERO;

    @NotNull(message = "O status da sessão é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessaoCaixaSituacaoEnum status;

    @NotNull(message = "A data de abertura é obrigatória.")
    @Column(name = "aberto_em", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "fechado_em")
    private LocalDateTime fechadoAt;

    @Column(name = "total_dinheiro", precision = 15, scale = 2)
    private BigDecimal totalDinheiro;

    @Column(name = "total_cartao", precision = 15, scale = 2)
    private BigDecimal totalCartao;

    @Column(name = "total_pix", precision = 15, scale = 2)
    private BigDecimal totalPix;

    @Column(name = "total_sangria", precision = 15, scale = 2)
    private BigDecimal totalSangria;

    @Column(name = "observacoes")
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(LocalDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
