package ru.hse.mmstr_project.se;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.hse.mmstr_project.se.storage.fast_storage.dto.IncidentMetadataDto;
import ru.hse.mmstr_project.se.storage.fast_storage.repository.RedisItemRepository;

@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
@EnableRedisRepositories
public class EmergencyNotificationApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EmergencyNotificationApplication.class, args);
        RedisItemRepository bean = run.getBean(RedisItemRepository.class);


        bean.save(new IncidentMetadataDto(1L, 1112L));
        bean.save(new IncidentMetadataDto(4L, 12L));
        bean.save(new IncidentMetadataDto(111L, 111211L));

        System.out.println(bean.findByFirstTimeToActivateLessThan(123));
        System.out.println(bean.findById(1L));
        System.out.println(bean.findById(111L));
        System.out.println(bean.findById(5L));
    }
}
