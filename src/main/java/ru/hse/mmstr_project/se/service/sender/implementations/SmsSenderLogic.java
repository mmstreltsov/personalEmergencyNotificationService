package ru.hse.mmstr_project.se.service.sender.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class SmsSenderLogic implements CommonSenderLogic {

    private static final String BASE_URL = "https://api.exolve.ru/messaging/v1/SendSMS";

    private final String apiKey;
    private final String sourceNumber;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SmsSenderLogic(
            @Qualifier("httpClientForSms") HttpClient httpClient,
            @Value("${sms.sender.mts.exolve.token}") String apiKey,
            @Value("${sms.sender.mts.exolve.number}") String sourceNumber,
            ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
        this.sourceNumber = sourceNumber;
        this.objectMapper = objectMapper;
    }


    @Override
    public boolean sendMessage(SenderRequestDto message) {
        try {
            return sendMessageImpl(message);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendMessageImpl(SenderRequestDto message) throws IOException, InterruptedException {
        String destinationNumber = message.phoneNumber();
        if (destinationNumber.isBlank() || destinationNumber.isEmpty()) {
            return true;
        }

        String body = objectMapper.writeValueAsString(new Body(
                sourceNumber,
                destinationNumber,
                prompt(message.text(), message.username(), message.telegramId())));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        return response.statusCode() == 200;
    }

    private static String prompt(String text, String username, String telegramId) {
        return String.format(
                "Привет, Ваш друг %s (telegramId %s) передает: %s. Свяжитесь с ним",
                username,
                telegramId,
                text);
    }

    private record Body(
            @JsonProperty("number") String sourceNumber,
            @JsonProperty("destination") String destinationNumber,
            @JsonProperty("text") String text) {
    }
}
