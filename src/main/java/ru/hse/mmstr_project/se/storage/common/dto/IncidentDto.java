package ru.hse.mmstr_project.se.storage.common.dto;

public class IncidentDto {
    private Long id;
    private Long clientId;
    private Double latitude;
    private Double longitude;

    public IncidentDto() {
    }

    public IncidentDto(Long id, Long clientId, Double latitude, Double longitude) {
        this.id = id;
        this.clientId = clientId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
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

    @Override
    public String toString() {
        return "IncidentDto{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}