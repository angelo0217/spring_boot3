package com.example.demo.service.integration;


import com.example.demo.entity.dto.HelloDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sample-service", url="${service.provider.url}", path="/demo")
public interface ProvideService {
    @GetMapping("/hello_word")
    HelloDTO getHelloWord();

}
