package com.br.manager.domain.stock.dto;

import com.br.manager.domain.stock.entity.StatusFuelEnum;
import com.br.manager.domain.stock.entity.UnitFuelEnum;

public class FuelInputDTO {
    private String name;
    private UnitFuelEnum unit;
    private StatusFuelEnum status;
    private String idAnp;

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

    public StatusFuelEnum getStatus() {
        return status;
    }

    public void setStatus(StatusFuelEnum active) {
        this.status = active;
    }

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }
}
