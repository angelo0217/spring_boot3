package com.example.demo.config.scheduller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ScheduleConfig {
    private static final ThreadGroup TEST_GROUP = new ThreadGroup("TestGroup");

    @Bean("TestScheduled")
    public ScheduledExecutorService timeOutScheduled() {
        return Executors.newScheduledThreadPool(
                10000,
                new ScheduleThreadFactory("test-scheduled", TEST_GROUP)
        );
    }


    @Bean("cronSchedulerThread")
    public ThreadPoolTaskScheduler cronSchedulerThread(){
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();
        return taskScheduler;
    }
}
