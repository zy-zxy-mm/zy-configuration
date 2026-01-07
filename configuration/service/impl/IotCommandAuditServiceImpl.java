package com.huafeng.modules.configuration.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huafeng.modules.configuration.entity.IotCommandAudit;
import com.huafeng.modules.configuration.mapper.IotCommandAuditMapper;
import com.huafeng.modules.configuration.service.IIotCommandAuditService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: IoT命令审计记录
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
public class IotCommandAuditServiceImpl extends ServiceImpl<IotCommandAuditMapper, IotCommandAudit> 
        implements IIotCommandAuditService {

    @Override
    public IotCommandAudit createAudit(String traceId, String operatorId, String operatorName,
                                        String deviceId, String commandKey, String paramsJson) {
        IotCommandAudit audit = new IotCommandAudit();
        audit.setTraceId(traceId);
        audit.setOperatorId(operatorId);
        audit.setOperatorName(operatorName);
        audit.setDeviceId(deviceId);
        audit.setCommandKey(commandKey);
        audit.setParamsJson(paramsJson);
        audit.setCreateTime(new Date());
        this.save(audit);
        return audit;
    }

    @Override
    public void updateAuditResult(String id, Integer resultCode, String resultMsg, Integer costMs) {
        IotCommandAudit audit = new IotCommandAudit();
        audit.setId(id);
        audit.setResultCode(resultCode);
        audit.setResultMsg(resultMsg);
        audit.setCostMs(costMs);
        this.updateById(audit);
    }
}
