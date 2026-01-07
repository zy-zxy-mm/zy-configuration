package com.huafeng.modules.configuration.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: IoT面板页面发布记录
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@TableName("iot_panel_page_publish")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "IoT面板页面发布记录")
public class IotPanelPagePublish implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**页面ID*/
    @Schema(description = "页面ID")
    private String pageId;

    /**版本ID*/
    @Schema(description = "版本ID")
    private String versionId;

    /**发布时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布时间")
    private Date publishTime;

    /**发布人*/
    @Schema(description = "发布人")
    private String publishBy;
}
