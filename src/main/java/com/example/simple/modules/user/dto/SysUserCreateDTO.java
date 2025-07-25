package com.example.simple.modules.user.dto;

import com.example.simple.annotation.XssSafe;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    @XssSafe
    private String nickname;
}