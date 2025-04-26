package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.hse.mmstr_project.se.service.storage.ClientStorage;
import ru.hse.mmstr_project.se.storage.common.dto.CreateClientDto;
import ru.hse.mmstr_project.se.storage.common.dto.CreateScenarioDto;
import ru.hse.mmstr_project.se.storage.common.entity.Scenario;
import ru.hse.mmstr_project.se.storage.common.mapper.ClientMapper;
import ru.hse.mmstr_project.se.storage.common.repository.ScenarioRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
@EnableRedisRepositories
@EnableScheduling
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EmergencyNotificationApplication.class, args);

        try {
            run.getBean(ClientStorage.class).save(new CreateClientDto("1", 11L, List.of()));
        } catch (Exception ignored) {
        }


        ClientMapper clientMapper = run.getBean(ClientMapper.class);
        for (int ahaha = 0; ; ahaha++) {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                return;
            }

            if (ahaha % 10000 == 0) {
                System.out.println("HELL Ahaha: " + ahaha);
            }

            List<Scenario> scenarioDtos = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                CreateScenarioDto createScenarioDto = new CreateScenarioDto(
                        "aahahhahahah",
                        1L,
                        List.of(),
                        Instant.now().plus(17 + 3 * i, ChronoUnit.SECONDS),
                        List.of(Instant.now().plus(17 + 3 * i, ChronoUnit.SECONDS), Instant.now().plus(32 + 2 * i, ChronoUnit.SECONDS)),
                        1,
                        true,
                        "heh"
                );
                scenarioDtos.add(clientMapper.toEntity(createScenarioDto));
            }

            run.getBean(ScenarioRepository.class).saveAll(scenarioDtos);
        }
    }
}
