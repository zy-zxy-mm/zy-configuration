package com.huafeng.modules.configuration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: IoT设备影子服务
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
@Slf4j
public class IotShadowService {

    private static final String SHADOW_KEY_PREFIX = "shadow:";
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired(required = false)
    private IotWebSocketService webSocketService;

    /**
     * 获取设备影子快照
     */
    public Map<String, Map<String, Object>> getSnapshot(List<String> deviceIds) {
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        for (String deviceId : deviceIds) {
            String key = SHADOW_KEY_PREFIX + deviceId;
            String json = redisTemplate.opsForValue().get(key);
            
            if (json != null) {
                try {
                    Map<String, Object> shadow = objectMapper.readValue(json, 
                            new TypeReference<Map<String, Object>>() {});
                    result.put(deviceId, shadow);
                } catch (Exception e) {
                    log.error("解析设备影子失败: deviceId={}", deviceId, e);
                    result.put(deviceId, new HashMap<>());
                }
            } else {
                // 返回空对象
                result.put(deviceId, new HashMap<>());
            }
        }
        
        return result;
    }

    /**
     * 更新设备影子
     */
    public void updateShadow(String deviceId, Map<String, Object> values) {
        String key = SHADOW_KEY_PREFIX + deviceId;
        
        // 获取现有影子
        Map<String, Object> shadow = new HashMap<>();
        String existingJson = redisTemplate.opsForValue().get(key);
        if (existingJson != null) {
            try {
                shadow = objectMapper.readValue(existingJson, 
                        new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("解析现有影子失败", e);
            }
        }
        
        // 合并更新
        shadow.putAll(values);
        
        // 保存到Redis
        try {
            String json = objectMapper.writeValueAsString(shadow);
            redisTemplate.opsForValue().set(key, json);
            
            // 推送WebSocket消息
            if (webSocketService != null) {
                webSocketService.pushDelta(deviceId, values);
            }
        } catch (Exception e) {
            log.error("保存设备影子失败", e);
        }
    }

    /**
     * 执行命令（模拟实现）
     */
    public void executeCommand(String deviceId, String commandKey, Map<String, Object> params) {
        log.info("执行命令: deviceId={}, commandKey={}, params={}", deviceId, commandKey, params);
        
        // 根据命令类型更新影子状态
        Map<String, Object> updates = new HashMap<>();
        
        switch (commandKey) {
            case "setPoint":
                // 设置点位值
                String point = (String) params.get("point");
                Object value = params.get("value");
                if (point != null) {
                    updates.put(point, value);
                }
                break;
            case "togglePower":
                // 切换电源状态
                updates.put("power", params.getOrDefault("power", false));
                break;
            case "setTemp":
                // 设置温度
                updates.put("tempSet", params.getOrDefault("value", 26));
                break;
            case "setMode":
                // 设置模式
                updates.put("mode", params.getOrDefault("mode", "cool"));
                break;
            case "setFanSpeed":
                // 设置风速
                updates.put("fanSpeed", params.getOrDefault("speed", "auto"));
                break;
            default:
                // 通用处理：将所有参数作为更新
                updates.putAll(params);
        }
        
        // 更新影子状态
        if (!updates.isEmpty()) {
            updateShadow(deviceId, updates);
        }
    }
}
