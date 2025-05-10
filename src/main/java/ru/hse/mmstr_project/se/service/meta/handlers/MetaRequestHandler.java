package ru.hse.mmstr_project.se.service.meta.handlers;

import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.MessageType;

import java.util.Optional;

public interface MetaRequestHandler {
    Optional<String> handle(MetaRequestDto requestDto);

    MessageType getMessageType();
}
