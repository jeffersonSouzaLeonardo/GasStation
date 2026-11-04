package com.br.manager.domain.venda_recibo.dto;

import java.time.Instant;
import java.util.UUID;

public class VendaReembolsoInputDTO {
    private UUID id;
    private UUID vendaId;
    private String motivo;
    private UUID requestedBy;
    private UUID aprovadoPor;
    private Instant reembolsadoEm;
    private String observacoes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVendaId() { return vendaId; }
    public void setVendaId(UUID vendaId) { this.vendaId = vendaId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public UUID getRequestedBy() { return requestedBy; }
    public void setRequestedBy(UUID requestedBy) { this.requestedBy = requestedBy; }
    public UUID getAprovadoPor() { return aprovadoPor; }
    public void setAprovadoPor(UUID aprovadoPor) { this.aprovadoPor = aprovadoPor; }
    public Instant getReembolsadoEm() { return reembolsadoEm; }
    public void setReembolsadoEm(Instant reembolsadoEm) { this.reembolsadoEm = reembolsadoEm; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
