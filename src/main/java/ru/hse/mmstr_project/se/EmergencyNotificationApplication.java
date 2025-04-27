package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
@EnableRedisRepositories
@EnableScheduling
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmergencyNotificationApplication.class, args);
    }
}
