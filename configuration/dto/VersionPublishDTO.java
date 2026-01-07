package com.huafeng.modules.configuration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description: 版本发布DTO
 * @Author: zhaoyang
 * @Date: 2026-01-05
 * @Version: V1.0
 */
@Data
@Schema(description = "版本发布DTO")
public class VersionPublishDTO {
    
    @NotBlank(message = "页面ID不能为空")
    @Schema(description = "页面ID")
    private String pageId;
    
    @NotBlank(message = "版本ID不能为空")
    @Schema(description = "版本ID")
    private String versionId;
}
