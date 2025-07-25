package com.example.simple.modules.user.vo;

import lombok.Data;

@Data
public class SysUserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
}