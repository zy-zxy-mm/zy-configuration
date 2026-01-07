package com.huafeng.modules.configuration.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huafeng.modules.configuration.entity.IotPanel;
import com.huafeng.modules.configuration.service.IIotPanelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

/**
 * @Description: IoT控制面板
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Tag(name = "IoT控制面板")
@RestController
@RequestMapping("/configuration/panel")
@Slf4j
public class IotPanelController {

    @Autowired
    private IIotPanelService iotPanelService;

    /**
     * 分页列表查询
     */
    @Operation(summary = "面板列表-分页查询")
    @GetMapping(value = "/list")
    public Result<IPage<IotPanel>> queryPageList(IotPanel iotPanel,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<IotPanel> queryWrapper = QueryGenerator.initQueryWrapper(iotPanel, req.getParameterMap());
        Page<IotPanel> page = new Page<>(pageNo, pageSize);
        IPage<IotPanel> pageList = iotPanelService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     */
    @AutoLog(value = "面板-添加")
    @Operation(summary = "面板-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody IotPanel iotPanel) {
        // 验证面板名称
        if (iotPanel.getPanelName() == null || iotPanel.getPanelName().trim().isEmpty()) {
            return Result.error("面板名称不能为空");
        }
        if (iotPanel.getPanelName().length() > 100) {
            return Result.error("面板名称不能超过100字符");
        }
        
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        iotPanel.setCreateBy(loginUser != null ? loginUser.getUsername() : "system");
        iotPanel.setCreateTime(new Date());
        iotPanelService.save(iotPanel);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "面板-编辑")
    @Operation(summary = "面板-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody IotPanel iotPanel) {
        // 验证面板名称
        if (iotPanel.getPanelName() == null || iotPanel.getPanelName().trim().isEmpty()) {
            return Result.error("面板名称不能为空");
        }
        if (iotPanel.getPanelName().length() > 100) {
            return Result.error("面板名称不能超过100字符");
        }
        
        IotPanel entity = iotPanelService.getById(iotPanel.getId());
        if (entity == null) {
            return Result.error("未找到对应数据");
        }
        
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        iotPanel.setUpdateBy(loginUser != null ? loginUser.getUsername() : "system");
        iotPanel.setUpdateTime(new Date());
        iotPanelService.updateById(iotPanel);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "面板-删除")
    @Operation(summary = "面板-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        iotPanelService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "面板-批量删除")
    @Operation(summary = "面板-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        iotPanelService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     */
    @Operation(summary = "面板-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<IotPanel> queryById(@RequestParam(name = "id") String id) {
        IotPanel iotPanel = iotPanelService.getById(id);
        if (iotPanel == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(iotPanel);
    }
}
