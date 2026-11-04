package com.br.manager.domain.aquisicao_preco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RecebimentoMercadoriaResponseDTO {
    private UUID id;
    private UUID fornecedorId;
    private UUID compraOrderId;
    private String numeroNota;
    private String chaveNota;
    private LocalDateTime recebidoEm;
    private String status;
    private BigDecimal valorFrete;
    private BigDecimal outrosCustos;
    private UUID recebidoPor;
    private List<ItemRecebimentoMercadoriaResponseDTO> itens;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(UUID fornecedorId) { this.fornecedorId = fornecedorId; }

    public UUID getCompraOrderId() { return compraOrderId; }
    public void setCompraOrderId(UUID compraOrderId) { this.compraOrderId = compraOrderId; }

    public String getNumeroNota() { return numeroNota; }
    public void setNumeroNota(String numeroNota) { this.numeroNota = numeroNota; }

    public String getChaveNota() { return chaveNota; }
    public void setChaveNota(String chaveNota) { this.chaveNota = chaveNota; }

    public LocalDateTime getRecebidoEm() { return recebidoEm; }
    public void setRecebidoEm(LocalDateTime recebidoEm) { this.recebidoEm = recebidoEm; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getOutrosCustos() { return outrosCustos; }
    public void setOutrosCustos(BigDecimal outrosCustos) { this.outrosCustos = outrosCustos; }

    public UUID getRecebidoPor() { return recebidoPor; }
    public void setRecebidoPor(UUID recebidoPor) { this.recebidoPor = recebidoPor; }

    public List<ItemRecebimentoMercadoriaResponseDTO> getItens() { return itens; }
    public void setItens(List<ItemRecebimentoMercadoriaResponseDTO> itens) { this.itens = itens; }
}
