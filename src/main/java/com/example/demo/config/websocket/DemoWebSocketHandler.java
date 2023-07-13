package com.example.demo.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Component
public class DemoWebSocketHandler extends AbstractWebSocketHandler {
    /**
     * websocket 連結成功
     *
     * @param webSocketSession
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        try{
            String type = (String) webSocketSession.getAttributes().get("type");
            log.info("open {}, type: {}", webSocketSession.getId(), type);
        } catch (Exception ex){
            log.error("[ws] open error ", ex);
        }
    }

    /**
     * 收到文字訊息
     *
     * @param webSocketSession
     * @param message
     */
    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
        try{
            log.info("msg: {}, length: {}", message.getPayload(), message.getPayload().length());
        } catch (Exception ex){
            log.error("[ws] open msg ", ex);
        }
    }


    /**
     * 收到二進制訊息
     *
     * @param webSocketSession
     * @param message
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession webSocketSession, BinaryMessage message) {
        try{
            //do nothing
        } catch (Exception ex) {
            log.error("[msg byte]", ex);
        }
        //do nothing
    }

    /**
     * 連線中斷後
     *
     * @param webSocketSession
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        try{
//            TODO
        } catch (Exception ex){
            log.error("[ws] open msg ", ex);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[---ws---] error {} ", session.getId(), exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}
/**
 *
 * // 创建 WebSocket 连接
 * const socket = new WebSocket('ws://localhost:9999/demo/chat_ws/1234');
 *
 * // 监听连接建立事件
 * socket.addEventListener('open', function (event) {
 *     console.log('WebSocket 连接已建立');
 *
 *     // 向服务器发送消息
 *     socket.send('Hello Server');
 * });
 *
 * // 监听消息接收事件
 * socket.addEventListener('message', function (event) {
 *     console.log('接收到服务器消息:', event.data);
 * });
 *
 * // 监听连接关闭事件
 * socket.addEventListener('close', function (event) {
 *     console.log('WebSocket 连接已关闭');
 * });
 *
 * // 监听连接错误事件
 * socket.addEventListener('error', function (event) {
 *     console.error('WebSocket 连接发生错误:', event);
 * });
 */