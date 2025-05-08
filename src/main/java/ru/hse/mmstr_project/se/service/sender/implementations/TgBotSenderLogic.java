package ru.hse.mmstr_project.se.service.sender.implementations;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

import java.util.List;

@Component
public class TgBotSenderLogic implements CommonSenderLogic {
    @Override
    public void sendMessage(List<SenderRequestDto> message) {
    }
}
