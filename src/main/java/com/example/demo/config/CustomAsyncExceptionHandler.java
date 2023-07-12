package com.example.demo.config;

import com.example.demo.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {

        log.error("Method name -  {}, {}" , method.getName(), JsonUtil.objectToJson(obj));
        log.error("Exception message -  {}" , throwable.getLocalizedMessage());
        log.error("Exception message -  {}" , throwable.getMessage());
        log.error("Exception message -  {}" , throwable.getCause());
    }

}