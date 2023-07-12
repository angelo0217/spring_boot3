package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduledFutureManager {
    private final Map<String, ScheduledFuture> stringScheduledFutureMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutorService;
    private ThreadPoolTaskScheduler taskScheduler;

    public ScheduledFutureManager(@Qualifier("TestScheduled") ScheduledExecutorService scheduledExecutorService,
                                  @Qualifier("cronSchedulerThread") ThreadPoolTaskScheduler taskScheduler){
        this.scheduledExecutorService = scheduledExecutorService;
        this.taskScheduler = taskScheduler;
    }


    public void addCronJob(String key, Runnable task, String cronExpression){
        this.remove(key);
        stringScheduledFutureMap.put(key, taskScheduler.schedule(task, new CronTrigger(cronExpression)));
    }

    public void addTestFuture(String key, Runnable task, int delay){
        this.remove(key);
        log.info("[ScheduledFutureManager](addTestFuture) key: {}", key);
        // 延迟 1 秒后执行任务
        stringScheduledFutureMap.put(key, scheduledExecutorService.schedule(task, delay, TimeUnit.SECONDS));
    }


    public void addTestFix(String key, Runnable task, int initialDelay, int period){
        this.remove(key);
        log.info("[ScheduledFutureManager](addTestFix) key: {}", key);
        // 延迟0 秒后开始执行任务，然后每 5 秒重复执行一次, initialDelay = 0, period = 5
        stringScheduledFutureMap.put(key, scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS));
    }

    public void addTestFixDelay(String key, Runnable task, int initialDelay, int period){
        this.remove(key);
        log.info("[ScheduledFutureManager](addTestFixDelay) key: {}", key);
        // 延迟 0 秒后开始执行任务，然后等待上一次任务执行完成后再延迟 5 秒重复执行一次, initialDelay = 0, period = 5
        stringScheduledFutureMap.put(key, scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, period, TimeUnit.SECONDS));
    }

    public void remove(String key){
        ScheduledFuture scheduledFuture = stringScheduledFutureMap.get(key);
        if(scheduledFuture != null){
            log.debug("[ScheduledFutureManager](remove), name {}, key: {}", this.getClass().getSimpleName(), key);
            scheduledFuture.cancel(true);
            stringScheduledFutureMap.remove(key);
        }
    }
}