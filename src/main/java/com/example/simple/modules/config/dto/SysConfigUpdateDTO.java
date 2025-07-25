package com.example.simple.modules.config.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置更新DTO
 */
@Data
public class SysConfigUpdateDTO {

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 备注
     */
    private String remark;
}