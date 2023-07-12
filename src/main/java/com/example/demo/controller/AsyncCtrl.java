package com.example.demo.controller;

import com.example.demo.service.AsyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/async")
public class AsyncCtrl {
    private AsyncService asyncService;

    public AsyncCtrl(AsyncService asyncService){
        this.asyncService = asyncService;
    }

    @GetMapping("/call")
    public String send() throws InterruptedException {
        asyncService.asyncMethodWithConfiguredExecutor();
        return "success";
    }

}
