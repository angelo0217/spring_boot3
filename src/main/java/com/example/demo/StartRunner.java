package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Order(1)
public class StartRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("***runner*** 啟動伺服器後的第一個工作點");
    }

}
