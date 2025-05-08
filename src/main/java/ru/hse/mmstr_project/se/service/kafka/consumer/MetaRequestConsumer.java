package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.meta.MetaService;

import java.util.List;

@Component
public class MetaRequestConsumer implements BatchAcknowledgingMessageListener<String, Object> {

    private final MetaService manager;

    public MetaRequestConsumer(MetaService manager) {
        this.manager = manager;
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