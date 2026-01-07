package com.huafeng.modules.configuration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description: 版本保存DTO
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@Schema(description = "版本保存DTO")
public class VersionSaveDTO {
    
    @NotBlank(message = "页面ID不能为空")
    @Schema(description = "页面ID")
    private String pageId;
    
    @NotBlank(message = "配置JSON不能为空")
    @Schema(description = "配置JSON")
    private String configJson;
    
    @Schema(description = "变更说明")
    private String changeLog;
}
