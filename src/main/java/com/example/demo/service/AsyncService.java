package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncService {
    @Async("threadPoolTaskExecutor")
    public void asyncMethodWithConfiguredExecutor() throws InterruptedException {
        Thread.sleep(5000);
        log.info("==== Execute method with configured executor {} ", Thread.currentThread().getName());
    }
}
