package com.huafeng.modules.configuration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huafeng.modules.configuration.entity.IotPanelPage;

/**
 * @Description: IoT面板页面
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
public interface IIotPanelPageService extends IService<IotPanelPage> {
    
    /**
     * 删除页面及其关联的版本数据
     */
    void deleteWithVersions(String id);
}
