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
 * @Description: IoT命令审计记录
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@TableName("iot_command_audit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "IoT命令审计记录")
public class IotCommandAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**追踪ID*/
    @Schema(description = "追踪ID")
    private String traceId;

    /**操作人ID*/
    @Schema(description = "操作人ID")
    private String operatorId;

    /**操作人名称*/
    @Schema(description = "操作人名称")
    private String operatorName;

    /**设备ID*/
    @Schema(description = "设备ID")
    private String deviceId;

    /**命令键*/
    @Schema(description = "命令键")
    private String commandKey;

    /**参数JSON*/
    @Schema(description = "参数JSON")
    private String paramsJson;

    /**结果码*/
    @Schema(description = "结果码")
    private Integer resultCode;

    /**结果消息*/
    @Schema(description = "结果消息")
    private String resultMsg;

    /**耗时(毫秒)*/
    @Schema(description = "耗时(毫秒)")
    private Integer costMs;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
}
