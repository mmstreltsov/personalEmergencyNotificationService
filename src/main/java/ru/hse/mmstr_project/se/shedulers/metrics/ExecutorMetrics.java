package ru.hse.mmstr_project.se.shedulers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ExecutorMetrics {

    private final Counter fastExecutorErrors;
    private final Counter fastExecutorRejects;
    private final Counter commonExecutorErrors;
    private final Counter commonExecutorRejects;

    public ExecutorMetrics(MeterRegistry meterRegistry) {
        this.fastExecutorErrors = Counter.builder("executor.fast.errors.count")
                .description("Total amount of errors due to executor task")
                .tag("place", "executors")
                .register(meterRegistry);

        this.fastExecutorRejects = Counter.builder("executor.fast.rejects.count")
                .description("Total amount of rejects for tasks")
                .tag("place", "executors")
                .register(meterRegistry);

        this.commonExecutorErrors = Counter.builder("executor.common.errors.count")
                .description("Total amount of errors due to executor task")
                .tag("place", "executors")
                .register(meterRegistry);

        this.commonExecutorRejects = Counter.builder("executor.common.rejects.count")
                .description("Total amount of rejects for tasks")
                .tag("place", "executors")
                .register(meterRegistry);
    }

    public void incFastExecutorError() {
        fastExecutorErrors.increment();
    }

    public void incFastExecutorReject() {
        fastExecutorRejects.increment();
    }

    public void incCommonExecutorError() {
        commonExecutorErrors.increment();
    }

    public void incCommonExecutorReject() {
        commonExecutorRejects.increment();
    }
}
