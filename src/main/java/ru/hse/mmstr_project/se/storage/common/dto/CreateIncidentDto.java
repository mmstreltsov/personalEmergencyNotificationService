package ru.hse.mmstr_project.se.storage.common.dto;

public class CreateIncidentDto {
    private final Long clientId;
    private Double latitude;
    private Double longitude;

    public CreateIncidentDto(Long clientId) {
        this.clientId = clientId;
    }

    public CreateIncidentDto(Long clientId, Double latitude, Double longitude) {
        this.clientId = clientId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getClientId() {
        return clientId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

}