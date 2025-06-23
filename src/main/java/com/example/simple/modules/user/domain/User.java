package com.example.simple.modules.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    private String avatarUrl;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}