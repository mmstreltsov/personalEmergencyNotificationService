package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class EmergencyNotificationApplication {
	@Value("${kafka.username}")
	private String kafkaUsername;

	@Value("${kafka.password}")
	private String kafkaPassword;

    public static void main(String[] args) {
		SpringApplication.run(EmergencyNotificationApplication.class, args);
	}

	@PostConstruct
	public void init() {
		System.out.println("Kafka Username: " + kafkaUsername);
		System.out.println("Kafka Password: " + kafkaPassword);
	}
}
