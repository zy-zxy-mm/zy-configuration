package com.huafeng.modules.configuration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huafeng.modules.configuration.entity.IotCommandAudit;

/**
 * @Description: IoT命令审计记录
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
public interface IIotCommandAuditService extends IService<IotCommandAudit> {
    
    /**
     * 创建审计记录
     */
    IotCommandAudit createAudit(String traceId, String operatorId, String operatorName, 
                                 String deviceId, String commandKey, String paramsJson);
    
    /**
     * 更新审计结果
     */
    void updateAuditResult(String id, Integer resultCode, String resultMsg, Integer costMs);
}
