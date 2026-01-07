package com.huafeng.modules.configuration.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huafeng.modules.configuration.dto.VersionPublishDTO;
import com.huafeng.modules.configuration.dto.VersionSaveDTO;
import com.huafeng.modules.configuration.entity.IotPanelPageVersion;
import com.huafeng.modules.configuration.service.IIotPanelPageVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: IoT面板页面版本
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT面板页面版本")
@RestController
@RequestMapping("/configuration/panelPageVersion")
@Slf4j
public class IotPanelPageVersionController {

    @Autowired
    private IIotPanelPageVersionService versionService;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 版本列表查询
     */
    @Operation(summary = "版本列表-分页查询")
    @GetMapping(value = "/list")
    public Result<IPage<IotPanelPageVersion>> queryPageList(
            @RequestParam(name = "pageId") String pageId,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        
        LambdaQueryWrapper<IotPanelPageVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotPanelPageVersion::getPageId, pageId);
        queryWrapper.orderByDesc(IotPanelPageVersion::getVersionNo);
        
        Page<IotPanelPageVersion> page = new Page<>(pageNo, pageSize);
        IPage<IotPanelPageVersion> pageList = versionService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 保存新版本
     */
    @AutoLog(value = "版本-保存")
    @Operation(summary = "版本-保存")
    @PostMapping(value = "/save")
    public Result<IotPanelPageVersion> save(@RequestBody VersionSaveDTO dto) {
        // 验证JSON格式
        try {
            objectMapper.readTree(dto.getConfigJson());
        } catch (Exception e) {
            return Result.error("配置JSON格式不合法");
        }
        
        IotPanelPageVersion version = versionService.saveVersion(
                dto.getPageId(), dto.getConfigJson(), dto.getChangeLog());
        return Result.OK(version);
    }

    /**
     * 获取版本详情
     */
    @Operation(summary = "版本-获取详情")
    @GetMapping(value = "/get")
    public Result<IotPanelPageVersion> get(@RequestParam(name = "id") String id) {
        IotPanelPageVersion version = versionService.getById(id);
        if (version == null) {
            return Result.error("未找到对应版本");
        }
        return Result.OK(version);
    }

    /**
     * 发布版本
     */
    @AutoLog(value = "版本-发布")
    @Operation(summary = "版本-发布")
    @PostMapping(value = "/publish")
    public Result<String> publish(@RequestBody VersionPublishDTO dto) {
        versionService.publishVersion(dto.getPageId(), dto.getVersionId());
        return Result.OK("发布成功！");
    }
}
