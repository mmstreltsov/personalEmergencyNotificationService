package ru.hse.mmstr_project.se.service.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

@Component
public class SenderProducer {
    private final String tgTopicName;
    private final String emailTopicName;
    private final String smsTopicName;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SenderProducer(
            @Value("${kafka.topic.name.sender.tg}") String tgTopicName,
            @Value("${kafka.topic.name.sender.email}") String emailTopicName,
            @Value("${kafka.topic.name.sender.sms}") String smsTopicName,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.tgTopicName = tgTopicName;
        this.emailTopicName = emailTopicName;
        this.smsTopicName = smsTopicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageToTg(SenderRequestDto dto) {
        kafkaTemplate.send(tgTopicName, dto);
    }

    public void sendMessageToEmail(SenderRequestDto dto) {
        kafkaTemplate.send(emailTopicName, dto);
    }

    public void sendMessageToSms(SenderRequestDto dto) {
        kafkaTemplate.send(smsTopicName, dto);
    }
}
