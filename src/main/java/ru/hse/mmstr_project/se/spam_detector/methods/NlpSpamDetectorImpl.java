package ru.hse.mmstr_project.se.spam_detector.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class NlpSpamDetectorImpl implements SpamDetector {

    private static final Logger log = LoggerFactory.getLogger(NlpSpamDetectorImpl.class);

    private static final String API_URL = "https://api-inference.huggingface.co/models/t-bank-ai/ruDialoGPT-small";
    private static final String NLP_PROMPT =
            "Сервис помощи людям в экстренной ситуации. Определи, является ли этот текст спамом, ответь только Да или Нет: ";
    private static final String YES = "да";

    private final String BEARER_TOKEN;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public NlpSpamDetectorImpl(
            @Value("${huggingface.token}") String bearerToken,
            HttpClient client,
            ObjectMapper objectMapper) {
        BEARER_TOKEN = bearerToken;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isSpam(String text) {
        try {
            return isSpamImpl(text);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    private boolean isSpamImpl(String text) throws IOException, InterruptedException {
        String payload = objectMapper.writeValueAsString(
                Map.of("inputs", NLP_PROMPT + text));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body().toLowerCase().contains(YES);
        } else {
            log.error("Can not detect if spam, api response code: {}", response.statusCode());
            return false;
        }
    }
}
