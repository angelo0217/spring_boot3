package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Hello{

    @GetMapping("/hello_word")
    public String helloWord(){
        log.debug("test hello");
        return "hello";
    }
}
