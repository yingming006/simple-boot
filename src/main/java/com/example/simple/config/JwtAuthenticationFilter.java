package com.example.simple.config;

import com.example.simple.interceptor.LoginUser;
import com.example.simple.modules.auth.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            // Token 不存在或格式不正确，直接放行，由后续的 Spring Security 过滤器进行处理
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(TOKEN_PREFIX.length());
            LoginUser loginUser = authService.verifyAccessToken(token);

            // 创建一个已认证的 Authentication 对象
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            // 将认证信息设置到 Spring Security 的上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Token 验证失败，SecurityContext 中将没有 Authentication 对象
            // 请求将被 Spring Security 的后续机制拦截
            logger.warn("JWT token verification failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}