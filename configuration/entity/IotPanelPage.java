package com.huafeng.modules.configuration.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: IoT面板页面
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@TableName("iot_panel_page")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "IoT面板页面")
public class IotPanelPage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**面板ID*/
    @Schema(description = "面板ID")
    private String panelId;

    /**页面名称*/
    @Schema(description = "页面名称")
    private String pageName;

    /**画布宽度*/
    @Schema(description = "画布宽度")
    private Integer canvasWidth;

    /**画布高度*/
    @Schema(description = "画布高度")
    private Integer canvasHeight;

    /**状态 0草稿 1已发布*/
    @Schema(description = "状态 0草稿 1已发布")
    private Integer status;

    /**创建人*/
    @Schema(description = "创建人")
    private String createBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /**更新人*/
    @Schema(description = "更新人")
    private String updateBy;

    /**更新时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**是否删除 0否 1是*/
    @Schema(description = "是否删除 0否 1是")
    @TableLogic
    private Integer delFlag;
}
