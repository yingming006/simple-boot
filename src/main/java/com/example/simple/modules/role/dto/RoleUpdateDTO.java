package com.example.simple.modules.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateDTO {
    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String description;
}