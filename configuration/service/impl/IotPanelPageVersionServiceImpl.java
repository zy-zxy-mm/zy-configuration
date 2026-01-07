package com.huafeng.modules.configuration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huafeng.modules.configuration.entity.IotPanelPage;
import com.huafeng.modules.configuration.entity.IotPanelPagePublish;
import com.huafeng.modules.configuration.entity.IotPanelPageVersion;
import com.huafeng.modules.configuration.mapper.IotPanelPageMapper;
import com.huafeng.modules.configuration.mapper.IotPanelPagePublishMapper;
import com.huafeng.modules.configuration.mapper.IotPanelPageVersionMapper;
import com.huafeng.modules.configuration.service.IIotPanelPageVersionService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: IoT面板页面版本
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
public class IotPanelPageVersionServiceImpl extends ServiceImpl<IotPanelPageVersionMapper, IotPanelPageVersion> 
        implements IIotPanelPageVersionService {

    @Autowired
    private IotPanelPageMapper pageMapper;
    
    @Autowired
    private IotPanelPagePublishMapper publishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IotPanelPageVersion saveVersion(String pageId, String configJson, String changeLog) {
        // 获取当前最大版本号
        Integer maxVersionNo = baseMapper.getMaxVersionNo(pageId);
        
        // 获取当前用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        
        // 创建新版本
        IotPanelPageVersion version = new IotPanelPageVersion();
        version.setPageId(pageId);
        version.setVersionNo(maxVersionNo + 1);
        version.setConfigJson(configJson);
        version.setChangeLog(changeLog);
        version.setIsPublished(0);
        version.setCreateBy(loginUser != null ? loginUser.getUsername() : "system");
        version.setCreateTime(new Date());
        
        this.save(version);
        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishVersion(String pageId, String versionId) {
        // 获取当前用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = loginUser != null ? loginUser.getUsername() : "system";
        
        // 将该页面所有版本设为未发布
        this.update(new LambdaUpdateWrapper<IotPanelPageVersion>()
                .eq(IotPanelPageVersion::getPageId, pageId)
                .set(IotPanelPageVersion::getIsPublished, 0));
        
        // 将指定版本设为已发布
        this.update(new LambdaUpdateWrapper<IotPanelPageVersion>()
                .eq(IotPanelPageVersion::getId, versionId)
                .set(IotPanelPageVersion::getIsPublished, 1));
        
        // 更新或插入发布记录
        IotPanelPagePublish existingPublish = publishMapper.selectOne(
                new LambdaQueryWrapper<IotPanelPagePublish>()
                        .eq(IotPanelPagePublish::getPageId, pageId));
        
        if (existingPublish != null) {
            existingPublish.setVersionId(versionId);
            existingPublish.setPublishTime(new Date());
            existingPublish.setPublishBy(username);
            publishMapper.updateById(existingPublish);
        } else {
            IotPanelPagePublish publish = new IotPanelPagePublish();
            publish.setPageId(pageId);
            publish.setVersionId(versionId);
            publish.setPublishTime(new Date());
            publish.setPublishBy(username);
            publishMapper.insert(publish);
        }
        
        // 更新页面状态为已发布
        IotPanelPage page = new IotPanelPage();
        page.setId(pageId);
        page.setStatus(1);
        pageMapper.updateById(page);
    }

    @Override
    public String getPublishedConfigJson(String pageId) {
        // 查询发布记录
        IotPanelPagePublish publish = publishMapper.selectOne(
                new LambdaQueryWrapper<IotPanelPagePublish>()
                        .eq(IotPanelPagePublish::getPageId, pageId));
        
        if (publish == null) {
            return null;
        }
        
        // 获取版本配置
        IotPanelPageVersion version = this.getById(publish.getVersionId());
        return version != null ? version.getConfigJson() : null;
    }

    @Override
    public String getVersionConfigJson(String pageId, String versionId) {
        IotPanelPageVersion version = this.getOne(
                new LambdaQueryWrapper<IotPanelPageVersion>()
                        .eq(IotPanelPageVersion::getId, versionId)
                        .eq(IotPanelPageVersion::getPageId, pageId));
        return version != null ? version.getConfigJson() : null;
    }
}
