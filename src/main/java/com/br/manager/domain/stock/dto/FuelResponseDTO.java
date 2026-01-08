package com.br.manager.domain.stock.dto;

import com.br.manager.domain.stock.enums.StatusFuelEnum;
import com.br.manager.domain.stock.enums.UnitFuelEnum;

public class FuelResponseDTO {
    private Long id;
    private String name;
    private UnitFuelEnum unit;
    private StatusFuelEnum status;
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
        return unit;
    }

    public void setUnit(UnitFuelEnum unit) {
        this.unit = unit;
    }

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public StatusFuelEnum getStatus() {
        return status;
    }

    public void setStatus(StatusFuelEnum status) {
        this.status = status;
    }
}
