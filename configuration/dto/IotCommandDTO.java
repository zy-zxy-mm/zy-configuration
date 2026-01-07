package com.huafeng.modules.configuration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * @Description: IoT命令DTO
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@Schema(description = "IoT命令DTO")
public class IotCommandDTO {
    
    @Schema(description = "追踪ID")
    private String traceId;
    
    @NotBlank(message = "设备ID不能为空")
    @Schema(description = "设备ID")
    private String deviceId;
    
    @NotBlank(message = "命令键不能为空")
    @Schema(description = "命令键")
    private String commandKey;
    
    @Schema(description = "参数")
    private Map<String, Object> params;
}
