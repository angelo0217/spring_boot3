package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.utils.JsonUtil;
import java.time.Instant;
import java.time.LocalDateTime;
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
        // 获取当前的日期和时间
        LocalDateTime now = LocalDateTime.now();

        // 获取年、月、日
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        // 打印结果
        System.out.println("年: " + year);
        System.out.println("月: " + month);
        System.out.println("日: " + day);

        Instant timestamp = Instant.now();

        // 打印结果
        System.out.println("当前时间戳（毫秒）: " + timestamp.toEpochMilli());
        System.out.println("当前时间戳（秒）: " + timestamp.getEpochSecond());
    }
}
