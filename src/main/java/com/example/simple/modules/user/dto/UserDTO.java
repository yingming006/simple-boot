package com.example.simple.modules.user.dto;

import lombok.Data;

@Data
public class UserDTO {

    /**
     * 筛选条件：用户昵称 (模糊查询)
     */
    private String nickname;

    /**
     * 筛选条件：用户名 (模糊查询)
     */
    private String username;
}