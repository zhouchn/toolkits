package com.zch.toolkits.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "spring.scheduling.enabled", havingValue = "true")
public class SchedulingConfig {
    @Value("${spring.scheduling.pool.size}")
    private int poolSize;
    @Value("${spring.scheduling.pool.thread-name-prefix}")
    private String threadNamePrefix;
    @Value("${spring.scheduling.pool.await-termination-millis}")
    private int awaitTerminationMillis;
    @Value("${spring.scheduling.pool.wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Bean(name = "taskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize); // 设置线程池大小
        scheduler.setThreadNamePrefix(threadNamePrefix); // 设置线程名称前缀
        scheduler.setAwaitTerminationMillis(awaitTerminationMillis); // 设置等待终止的时间（毫秒）
        scheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        scheduler.initialize();
        return scheduler;
    }
}
