package ru.hse.mmstr_project.se.shedulers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SchedulersContext {

    @Bean(name = "taskExecutorForCommonStorage")
    public Executor taskExecutorForCommonStorage() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setThreadNamePrefix("common-data-processor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutorForFastStorage")
    public Executor taskExecutorForFastStorage() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setThreadNamePrefix("fast-data-processor-");
        executor.initialize();
        return executor;
    }
}
