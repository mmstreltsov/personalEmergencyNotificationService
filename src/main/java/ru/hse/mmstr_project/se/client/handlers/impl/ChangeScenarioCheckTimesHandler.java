package ru.hse.mmstr_project.se.client.handlers.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.hse.mmstr_project.se.client.handlers.CommandHandler;
import ru.hse.mmstr_project.se.kafka.dto.MetaRequestDto;
import ru.hse.mmstr_project.se.service.kafka.producer.MetaRequestService;
import ru.hse.mmstr_project.se.service.meta.EntityType;
import ru.hse.mmstr_project.se.service.meta.FunctionType;
import ru.hse.mmstr_project.se.storage.common.dto.ScenarioDto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ChangeScenarioCheckTimesHandler implements CommandHandler {

    private final MetaRequestService metaRequestService;

    public ChangeScenarioCheckTimesHandler(MetaRequestService metaRequestService) {
        this.metaRequestService = metaRequestService;
    }

    @Override
    public Optional<String> handle(String args, Long chatId, Message message) {
        Optional<String> idO = getArg(args, 0);
        if (idO.isEmpty()) {
            return Optional.of("Предоставьте айди сценария");
        }
        String id = idO.get();

        ScenarioDto.Builder builder = ScenarioDto.builder();
        try {
            UUID uuid = UUID.fromString(id);
            builder.uuid(uuid);
        } catch (Exception ignored) {
            builder.name(id);
        }
        ScenarioDto withId = builder.build();

        List<ScenarioDto> response = new ArrayList<>();

        boolean complexData = false;
        for (int i = 1; ; i++) {
            Optional<String> arg = getArg(args, i);
            if (arg.isEmpty()) {
                break;
            }
            String userInput = arg.get();

            if (complexData) {
                userInput = String.join(" ", getArg(args, i - 1).orElse(""), userInput);
            }

            try {
                Instant instant = parseTimestamp(userInput);
                response.add(withId.toBuilder().firstTimeToActivate(instant).build());
                complexData = false;
            } catch (Exception ex) {
                if (!complexData) {
                    complexData = true;
                } else {
                    return Optional.of(ex.getMessage());
                }
            }
        }

        metaRequestService.sendMessage(new MetaRequestDto(
                FunctionType.UPDATE,
                EntityType.SCENARIO,
                chatId,
                Optional.empty(),
                Optional.empty(),
                response));
        return Optional.empty();
    }

    public Instant parseTimestamp(String userInput) {
        try {
            if (userInput.matches("\\d+")) {
                return Instant.ofEpochSecond(Long.parseLong(userInput));
            }

            if (userInput.matches("\\d+\\.\\d+")) {
                return Instant.ofEpochMilli((long) (Double.parseDouble(userInput) * 1000));
            }

            if (userInput.contains("T")) {
                return Instant.parse(userInput);
            }

            List<DateTimeFormatter> formatters = Arrays.asList(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneOffset.UTC));

            for (DateTimeFormatter formatter : formatters) {
                try {
                    return ZonedDateTime.parse(userInput, formatter).toInstant();
                } catch (Exception ignored) {
                }
            }

            throw new IllegalArgumentException("Неподдерживаемый формат даты");
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось разобрать дату: " + userInput, e);
        }
    }

    @Override
    public String getCommand() {
        return "/set_check_times";
    }
}
