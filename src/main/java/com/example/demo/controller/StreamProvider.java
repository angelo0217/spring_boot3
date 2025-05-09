package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.utils.JsonUtil;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/stream")
public class StreamProvider {
    private StreamBridge streamBridge;

    public StreamProvider(StreamBridge streamBridge){
        this.streamBridge = streamBridge;
    }

    @GetMapping("/send")
    public String send() {
        User user = new User("stream test", 123, new Date(), BigDecimal.valueOf(123), Long.valueOf(12323));
        streamBridge.send("msg-out-0", user); // 直接发送 User 对象
        return "success";
    }

    @GetMapping("/group")
    public String send_group() {
        User user = new User("stream test", 123, new Date(), BigDecimal.valueOf(123), Long.valueOf(12323));
        streamBridge.send("demo2-out-1", user); // 修改为 demo2-out-1
        return "success";
    }

}
