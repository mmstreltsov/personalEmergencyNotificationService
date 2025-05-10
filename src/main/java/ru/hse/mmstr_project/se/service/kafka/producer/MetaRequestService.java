package ru.hse.mmstr_project.se.service.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;

@Service
public class MetaRequestService {
    private final String topicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MetaRequestService(
            @Value("${kafka.topic.name.meta-requests}") String topicName,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MetaRequestDto dto) {
        kafkaTemplate.send(topicName, dto);
    }
}
