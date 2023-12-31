package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CacheSampleService;
import com.example.demo.utils.JsonUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;


@RestController
@RequestMapping("/rds")
public class RedisCtrl {
    private RedisTemplate redisTemplate;
    private CacheSampleService cacheSampleService;

    public RedisCtrl(RedisTemplate redisTemplate, CacheSampleService cacheSampleService){
        this.redisTemplate = redisTemplate;
        this.cacheSampleService = cacheSampleService;
    }

    @GetMapping("/test")
    public User test(){
        User user=new User("aa@126.com", 123, new Date(), BigDecimal.valueOf(123), Long.valueOf(12323));
        ValueOperations<String, User> operations=redisTemplate.opsForValue();
        operations.set("com.neox", user);
        User user_read = operations.get("com.neox");
        System.out.println(JsonUtil.objectToJson(user_read));
        return user_read;
    }


    @GetMapping("/api_cache/{userId}")
    @Cacheable(value = "user_api", key="#userId", cacheManager = "testManager")
    public User getUser(@PathVariable("userId") int userId) {
        User user=new User("aa@126.com", 123, new Date(), BigDecimal.valueOf(123), Long.valueOf(12323));
        System.out.println("do not use cache");
        return user;
    }


    @GetMapping("/service_cache/{userId}")
    public User serviceUser(@PathVariable("userId") int userId) {
        var user = cacheSampleService.getUser(userId);
        return user;
    }
}
