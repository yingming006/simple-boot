package com.example.simple.modules.auth.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_token")
public class SysUserTokenEntity {
    private Long id;
    private Long userId;
    private String accessToken;
    private LocalDateTime accessTokenExpireTime;
    private String refreshToken;
    private LocalDateTime refreshTokenExpireTime;
    private Boolean isActive;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}