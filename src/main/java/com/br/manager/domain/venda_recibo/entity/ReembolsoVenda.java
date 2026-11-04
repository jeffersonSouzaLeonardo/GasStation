package com.br.manager.domain.venda_recibo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reembolso_venda")
public class ReembolsoVenda {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotBlank(message = "Reason é obrigatório.")
    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @NotNull(message = "Requested by é obrigatório.")
    @Column(name = "solicitado_por", nullable = false)
    private UUID requestedBy;

    @NotNull(message = "Aprovado by é obrigatório.")
    @Column(name = "aprovado_por", nullable = false)
    private UUID aprovadoPor;

    @NotNull(message = "Reembolsado at é obrigatório.")
    @Column(name = "reembolsado_em", nullable = false)
    private Instant reembolsadoEm;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UUID getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(UUID requestedBy) {
        this.requestedBy = requestedBy;
    }

    public UUID getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(UUID aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }

    public Instant getReembolsadoEm() {
        return reembolsadoEm;
    }

    public void setReembolsadoEm(Instant reembolsadoEm) {
        this.reembolsadoEm = reembolsadoEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
