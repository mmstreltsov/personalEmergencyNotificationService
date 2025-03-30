package ru.hse.mmstr_project.se.spam_detector.methods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;

@Component
public class NlpDeepseekSpamDetectorImpl extends AbstractApiModelsDetectorImpl implements SpamDetector {

    private static final Logger log = LoggerFactory.getLogger(NlpDeepseekSpamDetectorImpl.class);

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String JSON = "{'model': 'deepseek/deepseek-chat:free',\n" +
            " 'messages': [{'role': 'user', 'content': '%s'}]}";
    private static final String NLP_PROMPT =
            "Сервис помощи людям в экстренной ситуации. Определи, является ли этот текст спамом, ответь только Да или Нет: ";

    public NlpDeepseekSpamDetectorImpl(
            @Value("${huggingface.token}") String bearerToken,
            HttpClient httpClientForMl) {
        super(httpClientForMl, bearerToken);
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
        String payload = String.format(JSON, NLP_PROMPT + text);
        return sendRequest(payload, API_URL);
    }
}
