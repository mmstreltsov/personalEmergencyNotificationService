package ru.hse.mmstr_project.se.spam_detector.methods;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RegexpSpamDetectorImpl implements SpamDetector {

    private static final Logger log = LoggerFactory.getLogger(RegexpSpamDetectorImpl.class);
    private List<Pattern> patterns;
    private final String regexFilePath;

    public RegexpSpamDetectorImpl(@Value("spam.regexp.text.file.path") String regexFilePath) {
        this.regexFilePath = regexFilePath;
    }

    @PostConstruct
    private void loadPatterns()  {
        try (InputStream inputStream = getClass().getResourceAsStream(regexFilePath)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + regexFilePath);
            }

            List<String> lines = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
            this.patterns = lines.stream()
                    .map(Pattern::compile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to load patterns: {}", e.getMessage());
        }
    }

    @Override
    public boolean isSpam(String text) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(text).find());
    }
}
