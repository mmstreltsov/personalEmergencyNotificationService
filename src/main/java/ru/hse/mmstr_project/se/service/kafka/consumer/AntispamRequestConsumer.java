package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.antispam.AntispamService;

import java.util.List;

@Component
public class AntispamRequestConsumer implements BatchAcknowledgingMessageListener<String, Object> {

    private final AntispamService manager;

    public AntispamRequestConsumer(AntispamService service) {
        this.manager = service;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Object>> records, Acknowledgment acknowledgment) {
        try {
            List<MetaRequestDto> batch = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(it -> it instanceof MetaRequestDto)
                    .map(it -> (MetaRequestDto) it)
                    .toList();
            manager.handle(batch);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
