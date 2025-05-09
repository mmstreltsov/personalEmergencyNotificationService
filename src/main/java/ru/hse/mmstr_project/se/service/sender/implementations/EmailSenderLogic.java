package ru.hse.mmstr_project.se.service.sender.implementations;

import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

@Component
public class EmailSenderLogic implements CommonSenderLogic {
    @Override
    public boolean sendMessage(SenderRequestDto message) {
        return true;
    }
}
