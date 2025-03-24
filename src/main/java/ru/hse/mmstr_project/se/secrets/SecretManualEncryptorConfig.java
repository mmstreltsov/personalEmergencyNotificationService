package ru.hse.mmstr_project.se.secrets;


import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretManualEncryptorConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(
            @Value("${jasypt.encryptor.password}") String password,
            @Value("${jasypt.encryptor.algorithm}") String algorithm) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
        return encryptor;
    }
}
