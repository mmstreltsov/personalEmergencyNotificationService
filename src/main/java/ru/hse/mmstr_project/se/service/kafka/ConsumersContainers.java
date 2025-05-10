package ru.hse.mmstr_project.se.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import ru.hse.mmstr_project.se.kafka.BaseConsumerConfig;
import ru.hse.mmstr_project.se.service.kafka.consumer.AntispamRequestConsumer;
import ru.hse.mmstr_project.se.service.kafka.consumer.EmailSenderConsumer;
import ru.hse.mmstr_project.se.service.kafka.consumer.MetaRequestConsumer;
import ru.hse.mmstr_project.se.service.kafka.consumer.MetaResponsesConsumer;
import ru.hse.mmstr_project.se.service.kafka.consumer.SmsSenderConsumer;
import ru.hse.mmstr_project.se.service.kafka.consumer.TgBotSenderConsumer;

@Configuration
@Import({
        BaseConsumerConfig.class,
})
public class ConsumersContainers {

    @Bean
    public KafkaMessageListenerContainer<String, Object> metaRequestContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            MetaRequestConsumer consumer,
            @Value("${kafka.topic.name.meta-requests}") String topic) {

        return getContainer(factory, consumer, topic, topic + "_1");
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> antispamRequestContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            AntispamRequestConsumer consumer,
            @Value("${kafka.topic.name.meta-requests}") String topic) {

        return getContainer(factory, consumer, topic, topic + "_2");
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> metaResponsesContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            MetaResponsesConsumer consumer,
            @Value("${kafka.topic.name.meta-responses}") String topic) {
        return getContainer(factory, consumer, topic, topic);
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> tgBotSenderContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            TgBotSenderConsumer consumer,
            @Value("${kafka.topic.name.sender.tg}") String topic) {
        return getContainer(factory, consumer, topic, topic);
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> emailSenderContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            EmailSenderConsumer consumer,
            @Value("${kafka.topic.name.sender.email}") String topic) {

        return getContainer(factory, consumer, topic, topic);
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> smsSenderContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            SmsSenderConsumer consumer,
            @Value("${kafka.topic.name.sender.sms}") String topic) {
        return getContainer(factory, consumer, topic, topic);
    }

    private <T> KafkaMessageListenerContainer<String, Object> getContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory,
            T consumer,
            String topic,
            String groupId) {
        ContainerProperties props = new ContainerProperties(topic);
        props.setGroupId(groupId);
        props.setMessageListener(consumer);
        props.setAckMode(ContainerProperties.AckMode.MANUAL);

        KafkaMessageListenerContainer<String, Object> container = new KafkaMessageListenerContainer<>(
                factory.getConsumerFactory(),
                props);
        container.setAutoStartup(true);
        return container;
    }
}
