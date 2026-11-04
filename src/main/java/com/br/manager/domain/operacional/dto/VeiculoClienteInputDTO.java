package com.br.manager.domain.operacional.dto;

import java.util.UUID;

public class VeiculoClienteInputDTO {
    private UUID id;
    private UUID clienteId;
    private String placa;
    private String marca;
    private String modelo;
    private String combustivelType;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getCombustivelType() { return combustivelType; }
    public void setCombustivelType(String combustivelType) { this.combustivelType = combustivelType; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
