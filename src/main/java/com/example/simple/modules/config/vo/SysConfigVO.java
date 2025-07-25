package com.example.simple.modules.config.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置视图对象
 */
@Data
public class SysConfigVO {
    private Long id;
    private String configKey;
    private String configName;
    private String configValue;
    private String remark;
    private LocalDateTime updateTime;
}