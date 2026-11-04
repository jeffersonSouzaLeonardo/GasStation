package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VendaInputDTO {
    private UUID id;
    private UUID estacaoId;
    private UUID turnoId;
    private String numeroVenda;
    private Instant vendidoEm;
    private UUID clienteId;
    private UUID veiculoId;
    private UUID frentistaUsuarioId;
    private UUID caixeiroUsuarioId;
    private BigDecimal valorDesconto;
    private SituacaoFiscal situacaoFiscal;
    private String observacoes;
    private List<VendaItemInputDTO> itens;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEstacaoId() { return estacaoId; }
    public void setEstacaoId(UUID estacaoId) { this.estacaoId = estacaoId; }
    public UUID getTurnoId() { return turnoId; }
    public void setTurnoId(UUID turnoId) { this.turnoId = turnoId; }
    public String getNumeroVenda() { return numeroVenda; }
    public void setNumeroVenda(String numeroVenda) { this.numeroVenda = numeroVenda; }
    public Instant getVendidoEm() { return vendidoEm; }
    public void setVendidoEm(Instant vendidoEm) { this.vendidoEm = vendidoEm; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public UUID getVeiculoId() { return veiculoId; }
    public void setVeiculoId(UUID veiculoId) { this.veiculoId = veiculoId; }
    public UUID getFrentistaUsuarioId() { return frentistaUsuarioId; }
    public void setFrentistaUsuarioId(UUID frentistaUsuarioId) { this.frentistaUsuarioId = frentistaUsuarioId; }
    public UUID getCaixeiroUsuarioId() { return caixeiroUsuarioId; }
    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) { this.caixeiroUsuarioId = caixeiroUsuarioId; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public SituacaoFiscal getSituacaoFiscal() { return situacaoFiscal; }
    public void setSituacaoFiscal(SituacaoFiscal situacaoFiscal) { this.situacaoFiscal = situacaoFiscal; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public List<VendaItemInputDTO> getItens() { return itens; }
    public void setItens(List<VendaItemInputDTO> itens) { this.itens = itens; }
}
