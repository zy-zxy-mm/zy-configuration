package com.huafeng.modules.configuration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huafeng.modules.configuration.entity.IotPanelPage;
import com.huafeng.modules.configuration.entity.IotPanelPagePublish;
import com.huafeng.modules.configuration.entity.IotPanelPageVersion;
import com.huafeng.modules.configuration.mapper.IotPanelPageMapper;
import com.huafeng.modules.configuration.mapper.IotPanelPagePublishMapper;
import com.huafeng.modules.configuration.mapper.IotPanelPageVersionMapper;
import com.huafeng.modules.configuration.service.IIotPanelPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: IoT面板页面
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
public class IotPanelPageServiceImpl extends ServiceImpl<IotPanelPageMapper, IotPanelPage> implements IIotPanelPageService {

    @Autowired
    private IotPanelPageVersionMapper versionMapper;
    
    @Autowired
    private IotPanelPagePublishMapper publishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithVersions(String id) {
        // 删除关联的版本
        versionMapper.delete(new LambdaQueryWrapper<IotPanelPageVersion>()
                .eq(IotPanelPageVersion::getPageId, id));
        // 删除关联的发布记录
        publishMapper.delete(new LambdaQueryWrapper<IotPanelPagePublish>()
                .eq(IotPanelPagePublish::getPageId, id));
        // 删除页面（逻辑删除）
        this.removeById(id);
    }
}
