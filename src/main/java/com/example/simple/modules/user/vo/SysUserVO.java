package com.example.simple.modules.user.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysUserVO implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
}