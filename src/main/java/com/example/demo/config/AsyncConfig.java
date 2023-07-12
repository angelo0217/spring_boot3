package com.example.demo.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private static final ThreadGroup TEST_GROUP = new ThreadGroup("TestGroup");

    @Bean("TestScheduled")
    public ScheduledExecutorService timeOutScheduled() {
        return Executors.newScheduledThreadPool(10000, new ScheduleThreadFactory("test-scheduled", TEST_GROUP));
    }


    @Bean("cronSchedulerThread")
    public ThreadPoolTaskScheduler cronSchedulerThread(){
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("MyThreadPool-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.setTaskDecorator(new LoggingTaskDecorator());

        executor.initialize();
        return executor;
    }


    private static class LoggingTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            return () -> {
                log.info("Thread name before execution: {}", Thread.currentThread().getName());
                try {
                    runnable.run();
                } finally {
                    log.info("Thread name after execution: {}", Thread.currentThread().getName());
                }
            };
        }
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
