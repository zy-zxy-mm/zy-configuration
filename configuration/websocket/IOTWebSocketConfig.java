package com.huafeng.modules.configuration.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Description: WebSocket配置
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Configuration
@EnableWebSocket
public class IOTWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private IotWebSocketHandler iotWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iotWebSocketHandler, "/ws/iot")
                .setAllowedOrigins("*");
    }
}
