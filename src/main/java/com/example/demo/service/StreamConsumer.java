package com.example.demo.service;


import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class StreamConsumer {
    private DispatchMsgService dispatchMsgService;
    public StreamConsumer(DispatchMsgService dispatchMsgService) {
        this.dispatchMsgService = dispatchMsgService;
    }

    @Bean
    Consumer<User> msg() {
        log.error("init stream consumer msg-in-0");
        return msg -> {
            log.info("stream consumer message => {}", msg);
            dispatchMsgService.dispatchMessage(msg);
        };
    }

    @Bean
    Consumer<User> test() {
        log.error("init test stream consumer test-in-1");
        return msg -> {
            log.info("stream test consumer message => {}", msg);
            dispatchMsgService.dispatchMessage(msg);
        };
    }
}
