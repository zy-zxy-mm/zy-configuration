package com.huafeng.modules.configuration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huafeng.modules.configuration.dto.IotCommandDTO;
import com.huafeng.modules.configuration.entity.IotCommandAudit;
import com.huafeng.modules.configuration.service.IIotCommandAuditService;
import com.huafeng.modules.configuration.service.IotShadowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @Description: IoT命令下发
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT命令下发")
@RestController
@RequestMapping("/iot")
@Slf4j
public class IotCommandController {

    @Autowired
    private IIotCommandAuditService auditService;
    
    @Autowired
    private IotShadowService shadowService;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 下发命令
     */
    @Operation(summary = "下发命令")
    @PostMapping(value = "/command")
    public Result<String> sendCommand(@RequestBody IotCommandDTO dto) {
        long startTime = System.currentTimeMillis();
        
        // 生成traceId
        String traceId = dto.getTraceId();
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        
        // 获取当前用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String operatorId = loginUser != null ? loginUser.getId() : "anonymous";
        String operatorName = loginUser != null ? loginUser.getRealname() : "匿名用户";
        
        // 创建审计记录
        String paramsJson;
        try {
            paramsJson = objectMapper.writeValueAsString(dto.getParams());
        } catch (Exception e) {
            paramsJson = "{}";
        }
        
        IotCommandAudit audit = auditService.createAudit(
                traceId, operatorId, operatorName,
                dto.getDeviceId(), dto.getCommandKey(), paramsJson);
        
        try {
            // 执行命令（通过影子服务模拟）
            shadowService.executeCommand(dto.getDeviceId(), dto.getCommandKey(), dto.getParams());
            
            // 更新审计结果
            long costMs = System.currentTimeMillis() - startTime;
            auditService.updateAuditResult(audit.getId(), 200, "success", (int) costMs);
            
            return Result.OK(traceId);
        } catch (Exception e) {
            log.error("命令执行失败", e);
            long costMs = System.currentTimeMillis() - startTime;
            auditService.updateAuditResult(audit.getId(), 500, e.getMessage(), (int) costMs);
            return Result.error("命令执行失败: " + e.getMessage());
        }
    }


    @Operation(summary = "测试")
    @PostMapping(value = "/test")
    public Result<String> test(@RequestBody IotCommandDTO dto) {
        System.out.println(dto);
        return Result.OK("success");
    }
}
