package ru.hse.mmstr_project.se.service.meta.handlers.impl;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;

@Component
public class DeleteClientHandlerImpl implements MetaRequestHandler {
    @Override
    public String handle(MetaRequestDto requestDto) {
        return "";
    }
}
