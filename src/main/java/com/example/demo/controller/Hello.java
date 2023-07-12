package com.example.demo.controller;

import com.example.demo.entity.TestDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Hello{

    @GetMapping("/hello_word")
    public String helloWord(){
        log.debug("test hello");
        return "hello";
    }

    @PostMapping("date")
    public TestDate date(@RequestBody TestDate testDate){
        System.out.println(testDate.getTestTime());
        return testDate;
    }
}
