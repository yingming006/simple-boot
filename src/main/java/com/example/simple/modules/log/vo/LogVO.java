package com.example.simple.modules.log.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogVO {
    private Long id;
    private Long userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ipAddress;
    private Long duration;
    private LocalDateTime createTime;
}