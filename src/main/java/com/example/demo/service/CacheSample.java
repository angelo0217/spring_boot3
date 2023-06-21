package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class CacheSample {

    @Cacheable(value = "user_service", key="#userId", cacheManager = "testManager")
    public User getUser(int userId) {
        User user=new User("aa@126.com", 123, new Date(), BigDecimal.valueOf(123), Long.valueOf(12323));
        System.out.println("create_user");
        return user;
    }
}
