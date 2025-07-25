package com.example.simple.modules.user.dto;

import lombok.Data;

@Data
public class SysUserPasswordUpdateDTO {

    private String oldPassword;

    private String newPassword;
}