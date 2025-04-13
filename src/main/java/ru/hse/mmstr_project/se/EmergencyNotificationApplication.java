package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.hse.mmstr_project.se.service.A;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.FriendDto;

import java.util.List;

@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EmergencyNotificationApplication.class, args);

        A bean = run.getBean(A.class);
        bean.ahahahah(new CreateClientDto(113L, 123L, List.of(FriendDto.builder()
                .id(1)
                .name("ahahah")
                .chatId(1)
                .aWayToNotify(List.of("tg"))
                .phoneNumber("+1")
                .build())));
    }
}
