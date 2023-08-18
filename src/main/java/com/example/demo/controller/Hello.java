package com.example.demo.controller;

import com.example.demo.entity.TestDate;
import com.example.demo.entity.dto.HelloDTO;
import com.example.demo.service.integration.ProvideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Hello{
    private ProvideService provideService;
    public Hello(ProvideService provideService){
        this.provideService = provideService;
    }

    @GetMapping("/hello_word")
    public HelloDTO helloWord(){
        log.debug("test hello");
//        builder 預設沒有無參數建構子，會死
//        var hello = HelloDTO.builder().word("hello").build();
        var hello = new HelloDTO();
        hello.setWord("hello");
        return hello;
    }

    @PostMapping("date")
    public TestDate date(@RequestBody TestDate testDate){
        System.out.println(testDate.getTestTime());
        return testDate;
    }

    @GetMapping("provider_service")
    public HelloDTO providerService(){
        return provideService.getHelloWord();
    }
}
