package com.example.demo.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class DemoWebSocketConfig implements WebSocketConfigurer {
    private static final String WS_USER_END_POINT = "/chat_ws/*";

    private DemoWebSocketHandler demoWebSocketHandler;

    public DemoWebSocketConfig(DemoWebSocketHandler demoWebSocketHandler){
        this.demoWebSocketHandler = demoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(demoWebSocketHandler, WS_USER_END_POINT).addInterceptors(new DemoWebSocketHandlerInterceptor()).setAllowedOrigins("*");
    }
}
