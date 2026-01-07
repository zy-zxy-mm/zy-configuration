package com.huafeng.modules.configuration.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huafeng.modules.configuration.entity.IotPanel;
import com.huafeng.modules.configuration.mapper.IotPanelMapper;
import com.huafeng.modules.configuration.service.IIotPanelService;
import org.springframework.stereotype.Service;

/**
 * @Description: IoT控制面板
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Service
public class IotPanelServiceImpl extends ServiceImpl<IotPanelMapper, IotPanel> implements IIotPanelService {
}
