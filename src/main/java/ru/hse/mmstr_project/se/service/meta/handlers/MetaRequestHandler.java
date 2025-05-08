package ru.hse.mmstr_project.se.service.meta.handlers;

import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;

public interface MetaRequestHandler {
    String handle(MetaRequestDto requestDto);
}
