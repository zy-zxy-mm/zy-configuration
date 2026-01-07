package com.huafeng.modules.configuration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huafeng.modules.configuration.entity.IotPanelPageVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: IoT面板页面版本
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Mapper
public interface IotPanelPageVersionMapper extends BaseMapper<IotPanelPageVersion> {
    
    /**
     * 获取页面的最大版本号
     */
    @Select("SELECT COALESCE(MAX(version_no), 0) FROM iot_panel_page_version WHERE page_id = #{pageId}")
    Integer getMaxVersionNo(@Param("pageId") String pageId);
}
