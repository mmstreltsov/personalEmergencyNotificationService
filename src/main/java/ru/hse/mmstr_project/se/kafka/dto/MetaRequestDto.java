package ru.hse.mmstr_project.se.kafka.dto;

import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;

import java.util.Optional;

public record MetaRequestDto(
        FunctionType functionType,
        EntityType entityType,
        Long chatId,
        Optional<Long> entityId,
        String data
) {
}
