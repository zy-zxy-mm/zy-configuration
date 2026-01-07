package com.huafeng.modules.configuration.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huafeng.modules.configuration.entity.IotPanelPage;
import com.huafeng.modules.configuration.service.IIotPanelPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description: IoT面板页面
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT面板页面")
@RestController
@RequestMapping("/configuration/panelPage")
@Slf4j
public class IotPanelPageController {

    @Autowired
    private IIotPanelPageService iotPanelPageService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "页面列表-分页查询")
    @GetMapping(value = "/list")
    public Result<IPage<IotPanelPage>> queryPageList(
            @RequestParam(name = "panelId") String panelId,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        
        LambdaQueryWrapper<IotPanelPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotPanelPage::getPanelId, panelId);
        queryWrapper.orderByDesc(IotPanelPage::getCreateTime);
        
        Page<IotPanelPage> page = new Page<>(pageNo, pageSize);
        IPage<IotPanelPage> pageList = iotPanelPageService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     */
    @AutoLog(value = "页面-添加")
    @Operation(summary = "页面-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody IotPanelPage iotPanelPage) {
        // 验证画布尺寸
        if (iotPanelPage.getCanvasWidth() != null && iotPanelPage.getCanvasWidth() < 100) {
            return Result.error("画布宽度不能小于100像素");
        }
        if (iotPanelPage.getCanvasHeight() != null && iotPanelPage.getCanvasHeight() < 100) {
            return Result.error("画布高度不能小于100像素");
        }
        
        // 设置默认值
        if (iotPanelPage.getCanvasWidth() == null) {
            iotPanelPage.setCanvasWidth(390);
        }
        if (iotPanelPage.getCanvasHeight() == null) {
            iotPanelPage.setCanvasHeight(844);
        }
        iotPanelPage.setStatus(0); // 默认草稿状态
        
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        iotPanelPage.setCreateBy(loginUser != null ? loginUser.getUsername() : "system");
        iotPanelPage.setCreateTime(new Date());
        
        iotPanelPageService.save(iotPanelPage);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "页面-编辑")
    @Operation(summary = "页面-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody IotPanelPage iotPanelPage) {
        // 验证画布尺寸
        if (iotPanelPage.getCanvasWidth() != null && iotPanelPage.getCanvasWidth() < 100) {
            return Result.error("画布宽度不能小于100像素");
        }
        if (iotPanelPage.getCanvasHeight() != null && iotPanelPage.getCanvasHeight() < 100) {
            return Result.error("画布高度不能小于100像素");
        }
        
        IotPanelPage entity = iotPanelPageService.getById(iotPanelPage.getId());
        if (entity == null) {
            return Result.error("未找到对应数据");
        }
        
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        iotPanelPage.setUpdateBy(loginUser != null ? loginUser.getUsername() : "system");
        iotPanelPage.setUpdateTime(new Date());
        
        iotPanelPageService.updateById(iotPanelPage);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除（级联删除版本）
     */
    @AutoLog(value = "页面-删除")
    @Operation(summary = "页面-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        iotPanelPageService.deleteWithVersions(id);
        return Result.OK("删除成功!");
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "页面-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<IotPanelPage> queryById(@RequestParam(name = "id") String id) {
        IotPanelPage iotPanelPage = iotPanelPageService.getById(id);
        if (iotPanelPage == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(iotPanelPage);
    }
}
