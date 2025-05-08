package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;
import ru.hse.mmstr_project.se.service.sender.implementations.CommonSenderLogic;

import java.util.List;

public abstract class CommonSenderConsumer implements BatchAcknowledgingMessageListener<String, Object> {

    protected final CommonSenderLogic manager;

    protected CommonSenderConsumer(CommonSenderLogic manager) {
        this.manager = manager;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Object>> records, Acknowledgment acknowledgment) {
        try {
            List<SenderRequestDto> batch = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(it -> it instanceof SenderRequestDto)
                    .map(it -> (SenderRequestDto) it)
                    .toList();
            manager.sendMessage(batch);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
