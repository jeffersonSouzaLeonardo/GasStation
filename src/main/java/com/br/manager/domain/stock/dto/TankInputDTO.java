package com.br.manager.domain.stock.dto;

import com.br.manager.domain.stock.entity.Fuel;

import java.math.BigDecimal;

public class TankInputDTO {
    private Long id;
    private Long fuel;
    private BigDecimal capacity;
    private String identity;
    private BigDecimal volume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFuel() {
        return fuel;
    }

    public void setFuel(Long fuel) {
        this.fuel = fuel;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
}
