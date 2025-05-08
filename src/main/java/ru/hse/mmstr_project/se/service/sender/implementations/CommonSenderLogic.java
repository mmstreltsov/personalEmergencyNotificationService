package ru.hse.mmstr_project.se.service.sender.implementations;


import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

import java.util.List;

public interface CommonSenderLogic {

    void sendMessage(List<SenderRequestDto> message);
}
