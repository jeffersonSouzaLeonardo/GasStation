package com.br.manager.domain.stock.entity;

import com.br.manager.domain.stock.enums.StatusFuelEnum;
import com.br.manager.domain.stock.enums.UnitFuelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Fuel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do combustível é obrigatório.")
    private String name;

    @NotNull(message = "A unidade de medida é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private UnitFuelEnum unit;

    @NotNull(message = "O Status do combustível é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusFuelEnum status;

    @Column(name = "id_anp")
    private String idAnp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public UnitFuelEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitFuelEnum unit) {
        this.unit = unit;
    }

    public StatusFuelEnum getStatus() {
        return status;
    }

    public void setStatus(StatusFuelEnum status) {
        this.status = status;
    }
}
