package com.huafeng.modules.configuration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huafeng.modules.configuration.entity.IotPanelPageVersion;

/**
 * @Description: IoT面板页面版本
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
public interface IIotPanelPageVersionService extends IService<IotPanelPageVersion> {
    
    /**
     * 保存新版本，版本号自动递增
     */
    IotPanelPageVersion saveVersion(String pageId, String configJson, String changeLog);
    
    /**
     * 发布版本
     */
    void publishVersion(String pageId, String versionId);
    
    /**
     * 获取已发布版本的配置JSON
     */
    String getPublishedConfigJson(String pageId);
    
    /**
     * 获取指定版本的配置JSON
     */
    String getVersionConfigJson(String pageId, String versionId);
}
