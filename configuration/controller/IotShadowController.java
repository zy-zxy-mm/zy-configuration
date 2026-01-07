package com.huafeng.modules.configuration.controller;

import com.huafeng.modules.configuration.service.IotShadowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: IoT设备影子状态
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT设备影子状态")
@RestController
@RequestMapping("/iot/shadow")
@Slf4j
public class IotShadowController {

    @Autowired
    private IotShadowService shadowService;

    /**
     * 获取设备影子快照
     */
    @Operation(summary = "获取设备影子快照")
    @GetMapping(value = "/snapshot")
    public Result<Map<String, Map<String, Object>>> getSnapshot(
            @RequestParam(name = "deviceIds") List<String> deviceIds) {
        Map<String, Map<String, Object>> snapshot = shadowService.getSnapshot(deviceIds);
        return Result.OK(snapshot);
    }
}
