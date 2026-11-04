package com.br.manager.domain.aquisicao_preco.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PedidoCompraInputDTO {

    private UUID id;
    private UUID fornecedorId;
    private String numero;
    private String status;
    private LocalDateTime pedidoEm;
    private LocalDateTime previstoEm;
    private UUID criadoPor;
    private List<ItemPedidoCompraInputDTO> itens;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(UUID fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPedidoEm() {
        return pedidoEm;
    }

    public void setPedidoEm(LocalDateTime pedidoEm) {
        this.pedidoEm = pedidoEm;
    }

    public LocalDateTime getPrevistoEm() {
        return previstoEm;
    }

    public void setPrevistoEm(LocalDateTime previstoEm) {
        this.previstoEm = previstoEm;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(UUID criadoPor) {
        this.criadoPor = criadoPor;
    }

    public List<ItemPedidoCompraInputDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoCompraInputDTO> itens) {
        this.itens = itens;
    }
}
