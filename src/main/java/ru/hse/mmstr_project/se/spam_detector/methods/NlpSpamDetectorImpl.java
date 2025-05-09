package ru.hse.mmstr_project.se.spam_detector.methods;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Map;

@Component
public class NlpSpamDetectorImpl extends AbstractApiModelsDetectorImpl implements SpamDetector {

    private static final Logger log = LoggerFactory.getLogger(NlpSpamDetectorImpl.class);

    private static final String API_URL = "https://api-inference.huggingface.co/models/t-bank-ai/ruDialoGPT-small";
    private static final String NLP_PROMPT =
            "Сервис помощи людям в экстренной ситуации. Определи, является ли этот текст спамом, ответь только Да или Нет: ";

    private final ObjectMapper objectMapper;

    public NlpSpamDetectorImpl(
            @Value("${huggingface.token}") String bearerToken,
            HttpClient httpClientForMl,
            ObjectMapper objectMapper) {
        super(httpClientForMl, bearerToken);
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
        String payload = objectMapper.writeValueAsString(Map.of("inputs", NLP_PROMPT + text));
        return sendRequest(payload, API_URL);
    }
}
