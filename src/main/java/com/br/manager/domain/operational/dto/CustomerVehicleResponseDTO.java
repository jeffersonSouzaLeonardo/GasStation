package com.br.manager.domain.operational.dto;

import java.util.UUID;

public class CustomerVehicleResponseDTO {
    private UUID id;
    private UUID customerId;
    private String plate;
    private String brand;
    private String model;
    private String fuelType;
    private Boolean active;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
