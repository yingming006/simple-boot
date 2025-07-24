package com.example.simple.modules.auth;

import com.example.simple.annotation.AuthIgnore;
import com.example.simple.annotation.RateLimit;
import com.example.simple.common.GlobalResponse;
import com.example.simple.interceptor.LoginUser;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.domain.RefreshTokenDTO;
import com.example.simple.modules.auth.domain.UserLoginDTO;
import com.example.simple.modules.auth.domain.UserRegisterDTO;
import com.example.simple.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @AuthIgnore
    @PostMapping("/register")
    public GlobalResponse<Void> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return GlobalResponse.success();
    }

    @RateLimit(count = 5, time = 10, limitType = RateLimit.LimitType.IP, message = "登录尝试过于频繁，请稍后重试")
    @AuthIgnore
    @PostMapping("/login")
    public GlobalResponse<LoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        // 1. 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        // 2. 认证成功后，获取 Principal（LoginUser）
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 3. 创建 Token
        LoginVO tokens = authService.createToken(loginUser);
        return GlobalResponse.success(tokens);
    }

    @AuthIgnore
    @PostMapping("/refresh")
    public GlobalResponse<LoginVO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO newTokens = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return GlobalResponse.success(newTokens);
    }

    @PostMapping("/logout")
    public GlobalResponse<Void> logout() {
        authService.logout();
        return GlobalResponse.success();
    }
}