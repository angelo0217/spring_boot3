package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/hello_word")
    public String helloWord(){
        stringRedisTemplate.opsForValue().set("123", "123");
        return "hello";
    }
}
