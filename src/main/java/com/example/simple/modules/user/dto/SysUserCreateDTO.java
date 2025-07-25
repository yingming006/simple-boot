package com.example.simple.modules.user.dto;

import com.example.simple.framework.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserCreateDTO {

    @NotBlank(message = "用户名不能为空")
    @UniqueUsername
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;
    private String avatarUrl;
}