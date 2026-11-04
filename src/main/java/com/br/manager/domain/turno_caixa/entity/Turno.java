package com.br.manager.domain.turno_caixa.entity;

import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID do posto é obrigatório.")
    @Column(name = "estacao_id", nullable = false)
    private UUID estacaoId;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    @Column(name = "caixeiro_usuario_id", nullable = false)
    private UUID caixeiroUsuarioId;

    @NotNull(message = "A data de abertura é obrigatória.")
    @Column(name = "aberto_em", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "fechado_em")
    private LocalDateTime fechadoAt;

    @NotNull(message = "O caixa inicial é obrigatório.")
    @DecimalMin(value = "0.00", message = "O caixa inicial não pode ser negativo.")
    @Column(name = "fundo_abertura", nullable = false, precision = 18, scale = 2)
    private BigDecimal fundoAbertura;

    @NotNull(message = "O status do turno é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TurnoSituacaoEnum status;

    @NotNull(message = "O usuário que abriu é obrigatório.")
    @Column(name = "aberto_por", nullable = false)
    private UUID openedBy;

    @Column(name = "fechado_por")
    private UUID fechadoBy;

    @Column(name = "nome", length = 120)
    private String nome;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlocacaoTurno> alocacoes = new ArrayList<>();

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeituraBico> leiturasBico = new ArrayList<>();

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentoCaixa> movimentosCaixa = new ArrayList<>();

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FechamentoPagamento> fechamentosPagamento = new ArrayList<>();
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEstacaoId() {
        return estacaoId;
    }

    public void setEstacaoId(UUID estacaoId) {
        this.estacaoId = estacaoId;
    }

    public UUID getCaixeiroUsuarioId() {
        return caixeiroUsuarioId;
    }

    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) {
        this.caixeiroUsuarioId = caixeiroUsuarioId;
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

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public TurnoSituacaoEnum getStatus() {
        return status;
    }

    public void setStatus(TurnoSituacaoEnum status) {
        this.status = status;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public UUID getFechadoBy() {
        return fechadoBy;
    }

    public void setFechadoBy(UUID fechadoBy) {
        this.fechadoBy = fechadoBy;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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

    public List<AlocacaoTurno> getAlocacoes() {
        return alocacoes;
    }

    public void setAlocacoes(List<AlocacaoTurno> alocacoes) {
        this.alocacoes = alocacoes;
    }

    public List<LeituraBico> getLeiturasBico() {
        return leiturasBico;
    }

    public void setLeiturasBico(List<LeituraBico> leiturasBico) {
        this.leiturasBico = leiturasBico;
    }

    public List<MovimentoCaixa> getMovimentosCaixa() {
        return movimentosCaixa;
    }

    public void setMovimentosCaixa(List<MovimentoCaixa> movimentosCaixa) {
        this.movimentosCaixa = movimentosCaixa;
    }

    public List<FechamentoPagamento> getFechamentosPagamento() {
        return fechamentosPagamento;
    }

    public void setFechamentosPagamento(List<FechamentoPagamento> fechamentosPagamento) {
        this.fechamentosPagamento = fechamentosPagamento;
    }
}
