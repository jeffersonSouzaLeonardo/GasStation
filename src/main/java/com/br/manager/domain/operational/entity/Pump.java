package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "pumps")
public class Pump {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Station ID is required.")
    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @NotBlank(message = "Pump code is required.")
    @Size(min = 1, max = 50, message = "Pump code must have between 1 and 50 characters.")
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "location", length = 100)
    private String location;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getStationId() { return stationId; }
    public void setStationId(UUID stationId) { this.stationId = stationId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
