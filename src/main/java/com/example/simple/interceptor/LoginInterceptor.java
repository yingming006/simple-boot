package com.example.simple.interceptor;

import com.example.simple.annotation.AuthIgnore;
import com.example.simple.modules.auth.AuthService;
import com.example.simple.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        AuthIgnore authIgnore = handlerMethod.getMethodAnnotation(AuthIgnore.class);
        if (authIgnore == null) {
            authIgnore = handlerMethod.getBeanType().getAnnotation(AuthIgnore.class);
        }

        if (authIgnore != null) {
            return true;
        }

        String authHeader = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            throw new BusinessException("用户未登录，请提供凭证");
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());

        LoginUser loginUser = authService.verifyAccessToken(token);

        UserContext.setLoginUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}