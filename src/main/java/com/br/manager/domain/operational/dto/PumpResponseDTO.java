package com.br.manager.domain.operational.dto;

import java.util.UUID;

public class PumpResponseDTO {
    private UUID id;
    private UUID stationId;
    private String code;
    private String location;
    private Boolean active;

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
