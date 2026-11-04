package com.br.manager.domain.aquisicao_preco.dto;

import java.util.List;
import java.util.UUID;

public class TabelaPrecoResponseDTO {
    private UUID id;
    private String nome;
    private String validoDe;
    private String validoAte;
    private String status;
    private UUID criadoPor;
    private List<ItemPrecoResponseDTO> itensPreco;

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

    public String getValidoDe() {
        return validoDe;
    }

    public void setValidoDe(String validoDe) {
        this.validoDe = validoDe;
    }

    public String getValidoAte() {
        return validoAte;
    }

    public void setValidoAte(String validoAte) {
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

    public List<ItemPrecoResponseDTO> getItensPreco() {
        return itensPreco;
    }

    public void setItensPreco(List<ItemPrecoResponseDTO> itensPreco) {
        this.itensPreco = itensPreco;
    }
}
