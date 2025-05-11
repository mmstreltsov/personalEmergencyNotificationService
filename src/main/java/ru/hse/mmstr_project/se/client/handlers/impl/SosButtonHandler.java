package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.TelegramBotMainSender;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.IncidentDto;
import ru.hse.mmstr_project.se.service.kafka.producer.IncidentProducer;

import java.util.Optional;

@Component
public class SosButtonHandler implements CommandHandler {

    private final TelegramBotMainSender sender;
    private final IncidentProducer incidentProducer;

    public SosButtonHandler(
            TelegramBotMainSender sender,
            IncidentProducer incidentProducer) {
        this.sender = sender;
        this.incidentProducer = incidentProducer;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        sender.requestLocationForSosButton(chatId);
        incidentProducer.sendMessage(new IncidentDto(chatId));

        return Optional.empty();
    }

    public void handleWithLocation(Long chatId, Location location) {
        incidentProducer.sendMessage(new IncidentDto(
                chatId,
                Optional.of(location.getLongitude()),
                Optional.of(location.getLatitude())));
    }

    @Override
    public String getCommand() {
        return "/sos";
    }
}
