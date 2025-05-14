package ru.hse.mmstr_project.se.spam_detector.methods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AbstractApiModelsDetectorImpl {

    private static final Logger log = LoggerFactory.getLogger(AbstractApiModelsDetectorImpl.class);
    private static final String YES = "да";

    private final HttpClient httpClient;
    private final String token;

    public AbstractApiModelsDetectorImpl(HttpClient httpClient, String bearerToken) {
        this.httpClient = httpClient;
        this.token = bearerToken;
    }

    public boolean sendRequest(String prompt, String apiUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(prompt, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body().toLowerCase().contains(YES);
        } else {
            log.error("Can not detect if spam, api response code: {}", response.statusCode());
            return false;
        }
    }
}
