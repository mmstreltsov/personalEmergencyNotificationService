package ru.hse.mmstr_project.se.service.sender.implementations;


import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

public interface CommonSenderLogic {
    boolean sendMessage(SenderRequestDto message);
}
