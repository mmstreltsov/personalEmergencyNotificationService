package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.client.TelegramBotMainSender;
import ru.hse.mmstr_project.se.kafka.dto.PingerDto;

import java.util.Optional;

@Component
public class PingerConsumer implements AcknowledgingMessageListener<String, Object> {

    private final TelegramBotMainSender manager;

    public PingerConsumer(TelegramBotMainSender manager) {
        this.manager = manager;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        Optional.ofNullable(record.value())
                .filter(it -> it instanceof PingerDto)
                .map(it -> (PingerDto) it)
                .ifPresent(it -> manager.sendMessage(it.chatId(), it.textToPing()));
        acknowledgment.acknowledge();
    }
}
