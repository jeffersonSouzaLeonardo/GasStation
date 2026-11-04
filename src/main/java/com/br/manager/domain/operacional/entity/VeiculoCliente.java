package com.br.manager.domain.operacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "veiculos_cliente")
public class VeiculoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID de Cliente é obrigatório.")
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @NotBlank(message = "Veiculo placa é obrigatório.")
    @Column(name = "placa", nullable = false, length = 10)
    private String placa;

    @Column(name = "marca", length = 50)
    private String marca;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Column(name = "tipo_combustivel", length = 30)
    private String combustivelType;

    @NotNull(message = "Ativo indicador é obrigatório.")
    @Column(name = "ativo", nullable = false)
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
