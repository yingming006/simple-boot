package com.example.simple.modules.auth.vo;

import com.example.simple.modules.user.vo.SysUserVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class LoginVO {
    private String accessToken;
    private String refreshToken;
    private SysUserVO userInfo;
    private Collection<String> permissions;

    public LoginVO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}