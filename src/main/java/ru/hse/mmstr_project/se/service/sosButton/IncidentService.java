package ru.hse.mmstr_project.se.service.sosButton;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.IncidentDto;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;
import ru.hse.mmstr_project.se.photo_service.PhotoByCoordinates;
import ru.hse.mmstr_project.se.service.sender.SenderService;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.service.storage.IncidentStorage;
import ru.hse.mmstr_project.se.storage.common.dto.ClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateIncidentDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IncidentService {

    private final static String HELP_TEXT = "Ваш друг нажал тревожную кнопку. Свяжитесь с ним!";
    private final static String HELP_TEXT_WITH_DATA = "Ваш друг прикрепил данные.";

    private final IncidentStorage incidentStorage;
    private final PhotoByCoordinates photoByCoordinates;
    private final ClientStorage clientStorage;
    private final SenderService senderService;

    public IncidentService(
            IncidentStorage incidentStorage,
            PhotoByCoordinates photoByCoordinates,
            ClientStorage clientStorage,
            SenderService senderService) {
        this.incidentStorage = incidentStorage;
        this.photoByCoordinates = photoByCoordinates;
        this.clientStorage = clientStorage;
        this.senderService = senderService;
    }

    @Transactional
    public void handle(List<IncidentDto> incidents) {
        Map<Long, ClientDto> chatIdToClient = clientStorage.findAllByChatIds(incidents.stream().map(IncidentDto::chatId).toList())
                .stream()
                .collect(Collectors.toMap(
                        ClientDto::getChatId,
                        Function.identity(),
                        (f, s) -> s));

        incidentStorage.saveAll(incidents.stream().map(IncidentDto::chatId).map(CreateIncidentDto::new).toList());

        incidents.forEach(inc -> Optional.ofNullable(chatIdToClient.get(inc.chatId())).ifPresent(cl -> handleOne(inc, cl)));

    }

    private void handleOne(IncidentDto incidentDto, ClientDto clientDto) {
        if (clientDto.getListOfFriends().isEmpty()) {
            return;
        }

        String helpText;
        byte[] photo;
        if (incidentDto.longitude().isPresent() && incidentDto.latitude().isPresent()) {
            Double longitude = incidentDto.longitude().get();
            Double latitude = incidentDto.latitude().get();

            helpText = HELP_TEXT_WITH_DATA + "Координаты: " + latitude + ", " + longitude + "; " + String.format("https://yandex.ru/maps/?ll=%s%%2C%s&z=18", longitude, latitude);
            photo = photoByCoordinates.getPhotoByCoordinates(longitude, latitude).orElse(null);
        } else {
            helpText = HELP_TEXT;
            photo = null;
        }

        clientDto.getListOfFriends().stream().map(
                friend -> new SenderRequestDto(
                        helpText,
                        photo,
                        clientDto.getName(),
                        clientDto.getTelegramId(),
                        friend.getWayToNotify(),
                        friend.getPhoneNumber(),
                        friend.getChatId(),
                        friend.getEmail())
        ).forEach(senderService::sendOne);
    }
}
