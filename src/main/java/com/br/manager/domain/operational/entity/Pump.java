package com.br.manager.domain.operational.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "pumps")
public class Pump {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "O código da bomba é obrigatório.")
    @Size(min = 1, max = 50, message = "O código da bomba deve ter entre 1 e 50 caracteres.")
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "location", length = 100)
    private String location;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
