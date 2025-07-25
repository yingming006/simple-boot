package com.example.simple.modules.auth;

import com.example.simple.annotation.AuthIgnore;
import com.example.simple.annotation.RateLimit;
import com.example.simple.common.GlobalResponse;
import com.example.simple.security.principal.SecurityPrincipal;
import com.example.simple.modules.auth.domain.LoginVO;
import com.example.simple.modules.auth.domain.RefreshTokenDTO;
import com.example.simple.modules.auth.domain.SysUserLoginDTO;
import com.example.simple.modules.auth.domain.SysUserRegisterDTO;
import com.example.simple.modules.user.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @AuthIgnore
    @PostMapping("/register")
    public GlobalResponse<Void> register(@RequestBody @Valid SysUserRegisterDTO registerDTO) {
        sysUserService.register(registerDTO);
        return GlobalResponse.success();
    }

    @AuthIgnore
    @RateLimit(count = 5, time = 10, limitType = RateLimit.LimitType.IP, message = "登录尝试过于频繁，请稍后重试")
    @PostMapping("/login")
    public GlobalResponse<LoginVO> login(@Valid @RequestBody SysUserLoginDTO loginDTO) {
        // 1. 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        // 2. 认证成功后，获取 Principal（LoginUser）
        SecurityPrincipal principal = (SecurityPrincipal) authentication.getPrincipal();

        // 3. 创建 Token
        LoginVO loginVO = authService.createToken(principal);

        // 4. 填充用户信息和权限列表
        loginVO.setUserInfo(sysUserService.getUserInfo(principal.getId()));
        loginVO.setPermissions(principal.getPermissions());

        return GlobalResponse.success(loginVO);
    }

    @AuthIgnore
    @PostMapping("/refresh")
    public GlobalResponse<LoginVO> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        // 注意：刷新Token接口目前只返回Token，如有需要也可按登录接口方式增强
        LoginVO newTokens = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return GlobalResponse.success(newTokens);
    }

    @PostMapping("/logout")
    public GlobalResponse<Void> logout() {
        authService.logout();
        return GlobalResponse.success();
    }
}