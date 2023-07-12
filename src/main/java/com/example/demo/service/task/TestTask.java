package com.example.demo.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("NewCustomerTimeOutTask")
@Slf4j
public class TestTask implements Runnable {

    private String name;

    public TestTask() {
        //do nothing
    }

    public TestTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        log.info("start run name: {}", name);
    }

}