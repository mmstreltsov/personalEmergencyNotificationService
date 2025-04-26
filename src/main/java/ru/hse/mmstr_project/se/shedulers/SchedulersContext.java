package ru.hse.mmstr_project.se.shedulers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.hse.mmstr_project.se.shedulers.metrics.ExecutorMetrics;

import java.util.concurrent.Executor;

@Configuration
public class SchedulersContext {

    @Bean(name = "taskExecutorForCommonStorage")
    public Executor taskExecutorForCommonStorage(ExecutorMetrics metrics) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);

        executor.setThreadNamePrefix("common-data-processor-");

        executor.setTaskDecorator(task -> () -> {
            try {
                task.run();
            } catch (Exception e) {
                metrics.incCommonExecutorError();
                throw e;
            }
        });

        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutorForFastStorage")
    public Executor taskExecutorForFastStorage(ExecutorMetrics metrics) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);

        executor.setThreadNamePrefix("fast-data-processor-");
        executor.setThreadPriority(Thread.MAX_PRIORITY - 1);

        executor.setTaskDecorator(task -> () -> {
            try {
                task.run();
            } catch (Exception e) {
                metrics.incFastExecutorError();
                throw e;
            }
        });

        executor.initialize();
        return executor;
    }
}
