package com.huafeng.modules.configuration.controller;

import com.huafeng.modules.configuration.service.IIotPanelPageVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: IoT面板运行态
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT面板运行态")
@RestController
@RequestMapping("/configuration/panelRuntime")
@Slf4j
public class IotPanelRuntimeController {

    @Autowired
    private IIotPanelPageVersionService versionService;

    /**
     * 获取已发布版本的配置JSON
     */
    @Operation(summary = "运行态-获取已发布配置")
    @GetMapping(value = "/getPublished")
    public Result<String> getPublished(@RequestParam(name = "pageId") String pageId) {
        String configJson = versionService.getPublishedConfigJson(pageId);
        if (configJson == null) {
            return Result.error("该页面尚未发布");
        }
        return Result.OK("查询成功！", configJson);
    }

    /**
     * 获取指定版本的配置JSON（用于预览）
     */
    @Operation(summary = "运行态-获取指定版本配置")
    @GetMapping(value = "/getVersion")
    public Result<String> getVersion(
            @RequestParam(name = "pageId") String pageId,
            @RequestParam(name = "versionId") String versionId) {
        String configJson = versionService.getVersionConfigJson(pageId, versionId);
        if (configJson == null) {
            return Result.error("未找到对应版本");
        }
        return Result.OK("查询成功",configJson);
    }
}
