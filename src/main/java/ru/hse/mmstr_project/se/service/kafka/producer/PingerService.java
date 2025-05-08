package ru.hse.mmstr_project.se.service.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.BaseProducerConfig;
import ru.hse.mmstr_project.se.kafka.dto.PingerDto;

@Component
@Import({
        BaseProducerConfig.class,
})
public class PingerService {

    private final String topicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PingerService(
            @Value("${kafka.topic.name.pinger}") String topicName,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PingerDto dto) {
        kafkaTemplate.send(topicName, dto);
    }
}
