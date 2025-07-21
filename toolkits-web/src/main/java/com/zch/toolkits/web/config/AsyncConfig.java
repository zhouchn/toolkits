package com.zch.toolkits.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@ConditionalOnProperty(name = "spring.async.enabled", havingValue = "true")
public class AsyncConfig {
    @Value("${spring.async.pool.core}")
    private int corePoolSize;
    @Value("${spring.async.pool.max}")
    private int maxPoolSize;
    @Value("${spring.async.pool.queue}")
    private int queueCapacity;
    @Value("${spring.async.pool.thread-name-prefix}")
    private String threadNamePrefix;
    @Value("${spring.async.pool.await-termination-millis}")
    private long awaitTerminationMillis;
    @Value("${spring.async.pool.wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Bean(name = "asyncTaskExecutor", destroyMethod = "shutdown")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setAwaitTerminationMillis(awaitTerminationMillis);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.initialize();
        return executor;
    }
}
