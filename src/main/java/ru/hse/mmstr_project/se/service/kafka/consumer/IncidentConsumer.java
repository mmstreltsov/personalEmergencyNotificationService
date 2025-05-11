package ru.hse.mmstr_project.se.service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.IncidentDto;
import ru.hse.mmstr_project.se.service.sosButton.IncidentService;

import java.util.List;

@Component
public class IncidentConsumer implements BatchAcknowledgingMessageListener<String, Object> {

    private final IncidentService manager;

    public IncidentConsumer(IncidentService manager) {
        this.manager = manager;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Object>> records, Acknowledgment acknowledgment) {
        try {
            List<IncidentDto> batch = records.stream()
                    .map(ConsumerRecord::value)
                    .filter(it -> it instanceof IncidentDto)
                    .map(it -> (IncidentDto) it)
                    .toList();
            manager.handle(batch);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}