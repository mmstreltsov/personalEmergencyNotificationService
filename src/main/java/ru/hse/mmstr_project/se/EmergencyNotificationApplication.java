package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
		SpringApplication.run(EmergencyNotificationApplication.class, args);
	}
}
