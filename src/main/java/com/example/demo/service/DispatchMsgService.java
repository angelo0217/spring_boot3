package com.example.demo.service;

import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DispatchMsgService {
    public void dispatchMessage(User user){
        log.debug("~~~~~~~~~~~~{}", user.getName());
    }
}
