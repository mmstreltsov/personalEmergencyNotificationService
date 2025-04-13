package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.hse.mmstr_project.se.service.A;

@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EmergencyNotificationApplication.class, args);

        A bean = run.getBean(A.class);
        bean.bots();
    }
}
