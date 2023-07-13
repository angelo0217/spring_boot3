package com.example.demo.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Slf4j
public class DemoWebSocketHandlerInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        log.debug("Before Handshake");
        if (request instanceof ServletServerHttpRequest) {
            var path = request.getURI().getPath();
            log.info("uri path: {}", path);
            var splitPath = path.split("/");
            if (splitPath.length > 0) {
                String type = splitPath[3];
                attributes.put("type", type);
            } else {
                log.warn("link error {}", path);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);

    }

}
