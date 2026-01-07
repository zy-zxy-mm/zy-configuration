package com.huafeng.modules.configuration.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huafeng.modules.configuration.service.IotWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @Description: IoT WebSocket处理器
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Component
@Slf4j
public class IotWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private IotWebSocketService webSocketService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        
        // 注册会话
        webSocketService.registerSession(sessionId, message -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("发送WebSocket消息失败", e);
            }
        });
        
        log.info("WebSocket连接建立: sessionId={}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();
        
        try {
            JsonNode json = objectMapper.readTree(payload);
            String type = json.path("type").asText();
            
            switch (type) {
                case "subscribe":
                    // 处理订阅请求
                    JsonNode subs = json.path("subs");
                    if (subs.isArray()) {
                        for (JsonNode sub : subs) {
                            String deviceId = sub.path("deviceId").asText();
                            if (!deviceId.isEmpty()) {
                                webSocketService.subscribe(sessionId, deviceId);
                            }
                        }
                    }
                    break;
                case "unsubscribe":
                    // 处理取消订阅
                    String deviceId = json.path("deviceId").asText();
                    if (!deviceId.isEmpty()) {
                        webSocketService.unsubscribe(sessionId, deviceId);
                    }
                    break;
                case "ping":
                    // 心跳响应
                    session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                    break;
                default:
                    log.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", payload, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        webSocketService.unregisterSession(sessionId);
        log.info("WebSocket连接关闭: sessionId={}, status={}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
    }
}
