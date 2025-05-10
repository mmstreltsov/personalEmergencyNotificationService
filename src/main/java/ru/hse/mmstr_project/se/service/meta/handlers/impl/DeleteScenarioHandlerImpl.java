package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.service.meta.MessageType;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;

import java.util.Optional;

@Component
public class DeleteScenarioHandlerImpl implements MetaRequestHandler {
    @Override
    public Optional<String> handle(MetaRequestDto requestDto) {
        return Optional.empty();
    }

    @Override
    public MessageType getMessageType() {
        return new MessageType(EntityType.SCENARIO, FunctionType.DELETE);
    }
}
