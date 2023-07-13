package com.example.demo.config.scheduller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Data
@Slf4j
public class ScheduleThreadFactory implements ThreadFactory {

    private int count = 1;

    private String prefixString;

    private ThreadGroup threadGroup;

    public ScheduleThreadFactory(String prefixString, ThreadGroup threadGroup) {
        this.prefixString = prefixString;
        this.threadGroup = threadGroup;
    }

    @Override
    public Thread newThread(Runnable r) {
        log.info("[schedule] Create group {} prefix {} thread #{}", threadGroup.getName(), prefixString, this.count);
        return new Thread(threadGroup, r, this.prefixString + "-" + this.count++);
    }

}