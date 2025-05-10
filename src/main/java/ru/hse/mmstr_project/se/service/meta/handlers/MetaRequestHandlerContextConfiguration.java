package ru.hse.mmstr_project.se.service.meta.handlers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hse.mmstr_project.se.service.meta.MessageType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MetaRequestHandlerContextConfiguration {

    @Bean
    public Map<MessageType, MetaRequestHandler> handlers(List<MetaRequestHandler> list) {
        return list.stream().collect(Collectors.toMap(
                MetaRequestHandler::getMessageType,
                Function.identity()));
    }
}
