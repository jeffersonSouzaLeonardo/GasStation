package com.br.manager.domain.aquisicao_preco.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TabelaPrecoInputDTO {
    private UUID id;
    private String nome;
    private LocalDateTime validoDe;
    private LocalDateTime validoAte;
    private String status;
    private UUID criadoPor;
    private List<ItemPrecoInputDTO> itensPreco;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getValidoDe() {
        return validoDe;
    }

    public void setValidoDe(LocalDateTime validoDe) {
        this.validoDe = validoDe;
    }

    public LocalDateTime getValidoAte() {
        return validoAte;
    }

    public void setValidoAte(LocalDateTime validoAte) {
        this.validoAte = validoAte;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(UUID criadoPor) {
        this.criadoPor = criadoPor;
    }

    public List<ItemPrecoInputDTO> getItensPreco() {
        return itensPreco;
    }

    public void setItensPreco(List<ItemPrecoInputDTO> itensPreco) {
        this.itensPreco = itensPreco;
    }
}
