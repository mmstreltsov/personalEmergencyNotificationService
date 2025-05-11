package ru.hse.mmstr_project.se.kafka.dto;

import java.util.Optional;

public record IncidentDto(
        Long chatId,
        Optional<Double> longitude,
        Optional<Double> latitude) {

    public IncidentDto(Long chatId) {
        this(chatId, Optional.empty(), Optional.empty());
    }
}
