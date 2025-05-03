package ru.hse.mmstr_project.se.shedulers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.hse.mmstr_project.se.shedulers.metrics.ExecutorMetrics;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Configuration
public class SchedulersContext {

    @Bean(name = "taskExecutorForCommonStorage")
    public Executor taskExecutorForCommonStorage(ExecutorMetrics metrics) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(64);
        executor.setThreadPriority(Thread.NORM_PRIORITY + 2);

        executor.setThreadNamePrefix("common-data-processor-");

        executor.setTaskDecorator(task -> () -> {
            try {
                task.run();
            } catch (Exception e) {
                metrics.incCommonExecutorError();
                throw e;
            }
        });
        executor.setRejectedExecutionHandler((task, exec) -> {
            metrics.incCommonExecutorReject();
            throw new RejectedExecutionException("Task " + task.toString() + " rejected from " + exec);
        });

        executor.initialize();
        return executor;
    }

    @Bean(name = "taskExecutorForFastStorage")
    public Executor taskExecutorForFastStorage(ExecutorMetrics metrics) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(128);

        executor.setThreadNamePrefix("fast-data-processor-");
        executor.setThreadPriority(Thread.NORM_PRIORITY + 1);

        executor.setTaskDecorator(task -> () -> {
            try {
                task.run();
            } catch (Exception e) {
                metrics.incFastExecutorError();
                throw e;
            }
        });
        executor.setRejectedExecutionHandler((task, exec) -> {
            metrics.incFastExecutorReject();
            throw new RejectedExecutionException("Task " + task.toString() + " rejected from " + exec);
        });

        executor.initialize();
        return executor;
    }
}
