package ru.hse.mmstr_project.se.service.meta;

import org.springframework.stereotype.Service;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.kafka.dto.TgBotRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaResponseService;
import ru.hse.mmstr_project.se.service.meta.handlers.MetaRequestHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MetaService {

    private final MetaResponseService responser;
    private final Map<MessageType, MetaRequestHandler> handlers;

    public MetaService(
            MetaResponseService responser,
            Map<MessageType, MetaRequestHandler> handlers) {
        this.responser = responser;
        this.handlers = handlers;
    }

    public void handle(List<MetaRequestDto> requests) {
        requests.stream().parallel().forEach(this::handleOne);
    }

    private void handleOne(MetaRequestDto request) {
        Optional<String> response;
        Long chatId = request.chatId();

        MessageType messageType = new MessageType(request.entityType(), request.functionType());
        try {
            response = Optional.ofNullable(handlers.get(messageType))
                    .map(it -> it.handle(request))
                    .orElse(Optional.of("Ошибка, не найден обработчик сообщения"));
        } catch (Exception e) {
            System.out.println(e);
            response = Optional.of(e.getMessage());
        }

        response.ifPresent(it -> responser.sendMessage(new TgBotRequestDto(it, chatId)));
    }
}
