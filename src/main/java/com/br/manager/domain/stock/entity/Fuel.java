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
    private UnitFuelEnum unitFuelEnum;

    @NotNull(message = "O Status do combustível é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusFuelEnum statusFuelEnum;

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

    public UnitFuelEnum getUnit() {
        return unitFuelEnum;
    }

    public void setUnit(UnitFuelEnum unit) {
        this.unitFuelEnum = unit;
    }

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public UnitFuelEnum getUnitFuelEnum() {
        return unitFuelEnum;
    }

    public void setUnitFuelEnum(UnitFuelEnum unitFuelEnum) {
        this.unitFuelEnum = unitFuelEnum;
    }

    public StatusFuelEnum getStatusFuelEnum() {
        return statusFuelEnum;
    }

    public void setStatusFuelEnum(StatusFuelEnum statusFuelEnum) {
        this.statusFuelEnum = statusFuelEnum;
    }
}
