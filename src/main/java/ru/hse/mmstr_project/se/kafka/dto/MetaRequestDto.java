package ru.hse.mmstr_project.se.kafka.dto;

import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.util.List;
import java.util.Optional;

public record MetaRequestDto(
        FunctionType functionType,
        EntityType entityType,
        Long chatId,
        Optional<ClientDto> clientDto,
        Optional<FriendDto> friendDto,
        List<ScenarioDto> scenarioDto,
        boolean isDeletingUpdate) {
}
