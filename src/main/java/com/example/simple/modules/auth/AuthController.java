package com.example.simple.modules.auth;

import com.example.simple.annotation.AuthIgnore;
import com.example.simple.annotation.RateLimit;
import com.example.simple.common.GlobalResponse;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.domain.RefreshTokenDTO;
import com.example.simple.modules.auth.domain.UserLoginDTO;
import com.example.simple.modules.auth.domain.UserRegisterDTO;
import com.example.simple.modules.user.entity.UserEntity;
import com.example.simple.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 用户注册
     */
    @AuthIgnore
    @PostMapping("/register")
    public GlobalResponse<Void> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return GlobalResponse.success();
    }

    /**
     * 用户登录
     */
    @RateLimit(count = 5, time = 10, limitType = RateLimit.LimitType.IP, message = "登录尝试过于频繁，请稍后重试")
    @AuthIgnore
    @PostMapping("/login")
    public GlobalResponse<LoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserEntity user = userService.validateUser(loginDTO);
        LoginVO tokens = authService.createToken(user);
        return GlobalResponse.success(tokens);
    }

    /**
     * 刷新Token
     */
    @AuthIgnore
    @PostMapping("/refresh")
    public GlobalResponse<LoginVO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO newTokens = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return GlobalResponse.success(newTokens);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public GlobalResponse<Void> logout() {
        authService.logout();
        return GlobalResponse.success();
    }
}