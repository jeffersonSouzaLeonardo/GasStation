package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "customer_vehicles")
public class CustomerVehicle {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Customer ID is required.")
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @NotBlank(message = "Vehicle plate is required.")
    @Column(name = "plate", nullable = false, length = 10)
    private String plate;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "fuel_type", length = 30)
    private String fuelType;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

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
