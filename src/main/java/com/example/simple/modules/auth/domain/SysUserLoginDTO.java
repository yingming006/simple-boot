package com.example.simple.modules.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserLoginDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}