package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.utils.JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestJava {

    @Test
    public void testObj() throws Exception {
        var url = "/demo/chat_ws/1234";
        String[] splitPath = url.split("/");
        System.out.println(splitPath);
    }
}