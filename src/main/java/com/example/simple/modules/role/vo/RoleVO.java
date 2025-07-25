package com.example.simple.modules.role.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleVO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
}