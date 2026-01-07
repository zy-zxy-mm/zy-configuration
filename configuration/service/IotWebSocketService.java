package com.huafeng.modules.configuration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: IoT WebSocket服务
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
@Slf4j
public class IotWebSocketService {

    @Autowired
    private ObjectMapper objectMapper;

    // 设备订阅关系: deviceId -> sessionIds
    private final Map<String, Set<String>> deviceSubscriptions = new ConcurrentHashMap<>();
    
    // 会话订阅关系: sessionId -> deviceIds
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();
    
    // WebSocket会话回调
    private final Map<String, WebSocketCallback> sessionCallbacks = new ConcurrentHashMap<>();

    /**
     * WebSocket回调接口
     */
    public interface WebSocketCallback {
        void sendMessage(String message);
    }

    /**
     * 注册会话
     */
    public void registerSession(String sessionId, WebSocketCallback callback) {
        sessionCallbacks.put(sessionId, callback);
        sessionSubscriptions.put(sessionId, new CopyOnWriteArraySet<>());
        log.info("WebSocket会话注册: sessionId={}", sessionId);
    }

    /**
     * 注销会话
     */
    public void unregisterSession(String sessionId) {
        // 清理订阅关系
        Set<String> devices = sessionSubscriptions.remove(sessionId);
        if (devices != null) {
            for (String deviceId : devices) {
                Set<String> sessions = deviceSubscriptions.get(deviceId);
                if (sessions != null) {
                    sessions.remove(sessionId);
                }
            }
        }
        sessionCallbacks.remove(sessionId);
        log.info("WebSocket会话注销: sessionId={}", sessionId);
    }

    /**
     * 订阅设备
     */
    public void subscribe(String sessionId, String deviceId) {
        // 添加设备订阅
        deviceSubscriptions.computeIfAbsent(deviceId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
        
        // 添加会话订阅
        Set<String> devices = sessionSubscriptions.get(sessionId);
        if (devices != null) {
            devices.add(deviceId);
        }
        
        log.info("订阅设备: sessionId={}, deviceId={}", sessionId, deviceId);
    }

    /**
     * 取消订阅设备
     */
    public void unsubscribe(String sessionId, String deviceId) {
        Set<String> sessions = deviceSubscriptions.get(deviceId);
        if (sessions != null) {
            sessions.remove(sessionId);
        }
        
        Set<String> devices = sessionSubscriptions.get(sessionId);
        if (devices != null) {
            devices.remove(deviceId);
        }
        
        log.info("取消订阅: sessionId={}, deviceId={}", sessionId, deviceId);
    }

    /**
     * 推送设备状态变更
     */
    public void pushDelta(String deviceId, Map<String, Object> values) {
        Set<String> sessions = deviceSubscriptions.get(deviceId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "delta");
            message.put("deviceId", deviceId);
            message.put("values", values);
            
            String json = objectMapper.writeValueAsString(message);
            
            for (String sessionId : sessions) {
                WebSocketCallback callback = sessionCallbacks.get(sessionId);
                if (callback != null) {
                    try {
                        callback.sendMessage(json);
                    } catch (Exception e) {
                        log.error("推送消息失败: sessionId={}", sessionId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("序列化消息失败", e);
        }
    }
}
