package ru.hse.mmstr_project.se.photo_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

@Service
public class PhotoByCoordinates {

    private static final Logger log = LoggerFactory.getLogger(PhotoByCoordinates.class);

    private final HttpClient httpClient;
    private final String API_TOKEN;

    private static final String BASE_URL = "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/" +
            "pin-l-marker+ff0000(%1$s,%2$s)/%1$s,%2$s,17/800x800?access_token=%3$s";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
            "#.######",
            new DecimalFormatSymbols(Locale.US));

    public PhotoByCoordinates(
            HttpClient httpClientForMapbox,
            @Value("${photomap.mapbox.token}") String apiToken) {
        this.httpClient = httpClientForMapbox;
        API_TOKEN = apiToken;
    }

    public Optional<byte[]> getPhotoByCoordinates(double longitude, double latitude) {
        String url = String.format(
                BASE_URL,
                DECIMAL_FORMAT.format(latitude),
                DECIMAL_FORMAT.format(longitude),
                API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                return Optional.of(response.body());
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
