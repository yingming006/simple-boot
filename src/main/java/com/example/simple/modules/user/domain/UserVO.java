package com.example.simple.modules.user.domain;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
}