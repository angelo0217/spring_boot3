package com.example.demo.service.aop;

import com.example.demo.entity.TestDate;
import com.example.demo.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DemoAop {

    @Pointcut("execution(* com.example.demo.controller.*.*(..))")
    public void executionController() {
    }

    @Before("executionController()")
    public void beforeController(JoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        log.info("***aop before*** requestMapping name : " + name);

        Object[] objs = joinPoint.getArgs();
        for (Object obj : objs) {
            if (obj instanceof TestDate date) {
                log.info("***aop before*** get req :{}", JsonUtil.objectToJson(date));
            }
        }
    }

    @AfterReturning(
        pointcut = "executionController()",
        returning = "result"
    )
    public void afterController(JoinPoint joinPoint, Object result) {
        log.info("***aop after*** request mapping ");
        log.info("***aop after*** request result :{} ", JsonUtil.objectToJson(result));
        Object[] objs = joinPoint.getArgs();
        for (Object obj : objs) {
            if (obj instanceof TestDate) {
                log.info("***aop after*** UserBookTestVo is : {}  ", JsonUtil.objectToJson(result));
            }
        }
    }
}
