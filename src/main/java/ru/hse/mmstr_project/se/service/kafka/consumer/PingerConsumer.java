package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.client.TelegramBotMainSender;
import ru.hse.mmstr_project.se.kafka.dto.PingerDto;

import java.util.List;

@Component
public class PingerConsumer implements BatchAcknowledgingMessageListener<String, Object> {

    private final TelegramBotMainSender manager;

    public PingerConsumer(TelegramBotMainSender manager) {
        this.manager = manager;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Object>> records, Acknowledgment acknowledgment) {
        try {
            records.stream()
                    .map(ConsumerRecord::value)
                    .filter(it -> it instanceof PingerDto)
                    .map(it -> (PingerDto) it)
                    .forEach(it -> {
                        manager.sendMessage(it.chatId(), it.textToPing());
                    });
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
