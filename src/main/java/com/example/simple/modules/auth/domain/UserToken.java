package com.example.simple.modules.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_token")
public class UserToken {
    private Long id;
    private Long userId;
    private String accessToken;
    private LocalDateTime accessTokenExpireTime;
    private String refreshToken;
    private LocalDateTime refreshTokenExpireTime;
    private Boolean isActive;
    private LocalDateTime createTime;
}