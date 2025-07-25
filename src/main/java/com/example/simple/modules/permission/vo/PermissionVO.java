package com.example.simple.modules.permission.vo;

import lombok.Data;
import java.util.List;

@Data
public class PermissionVO {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private Integer type;
    private List<PermissionVO> children;
}