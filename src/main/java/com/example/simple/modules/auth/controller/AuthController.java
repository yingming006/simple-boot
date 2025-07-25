package com.example.simple.modules.auth.controller;

import com.example.simple.annotation.AuthIgnore;
import com.example.simple.annotation.RateLimit;
import com.example.simple.common.GlobalResponse;
import com.example.simple.modules.auth.service.AuthService;
import com.example.simple.modules.auth.vo.LoginVO;
import com.example.simple.modules.auth.dto.RefreshTokenDTO;
import com.example.simple.modules.auth.dto.SysUserLoginDTO;
import com.example.simple.modules.auth.dto.SysUserRegisterDTO;
import com.example.simple.modules.user.service.SysUserService;
import com.example.simple.framework.security.principal.SecurityPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 * @author yingm
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 操作结果
     */
    @AuthIgnore
    @PostMapping("/register")
    public GlobalResponse<Void> register(@RequestBody @Valid SysUserRegisterDTO registerDTO) {
        sysUserService.register(registerDTO);
        return GlobalResponse.success();
    }

    /**
     * 用户登录
     * @param loginDTO 登录凭证
     * @return 包含Token、用户信息和权限的登录视图
     */
    @AuthIgnore
    @RateLimit(count = 5, time = 10, limitType = RateLimit.LimitType.IP, message = "登录尝试过于频繁，请稍后重试")
    @PostMapping("/login")
    public GlobalResponse<LoginVO> login(@Valid @RequestBody SysUserLoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        SecurityPrincipal principal = (SecurityPrincipal) authentication.getPrincipal();

        LoginVO loginVO = authService.createToken(principal);

        loginVO.setUserInfo(sysUserService.getUserInfo(principal.getId()));
        loginVO.setPermissions(principal.getPermissions());

        return GlobalResponse.success(loginVO);
    }

    /**
     * 刷新Token
     * @param refreshTokenDTO 包含刷新令牌的数据
     * @return 新的Token信息
     */
    @AuthIgnore
    @PostMapping("/refresh")
    public GlobalResponse<LoginVO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO newTokens = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return GlobalResponse.success(newTokens);
    }

    /**
     * 用户登出
     * @return 操作结果
     */
    @PostMapping("/logout")
    public GlobalResponse<Void> logout() {
        authService.logout();
        return GlobalResponse.success();
    }
}