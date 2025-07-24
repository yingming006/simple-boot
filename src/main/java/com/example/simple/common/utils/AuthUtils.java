package com.example.simple.common.utils;

import com.example.simple.interceptor.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    /**
     * 获取当前登录的用户信息对象
     * @return LoginUser, 如果未登录则返回null
     */
    public static LoginUser getLoginUser() { // <<<--- 修改为 public
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean isSuperAdmin() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && ROLE_SUPER_ADMIN.equals(loginUser.getRole());
    }

    public static boolean isAdmin() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && ROLE_ADMIN.equals(loginUser.getRole());
    }

    public static Long getCurrentUserId() {
        LoginUser loginUser = getLoginUser();
        return (loginUser != null) ? loginUser.getId() : null;
    }

    public static String getUserRole() {
        LoginUser loginUser = getLoginUser();
        return (loginUser != null) ? loginUser.getRole() : null;
    }
}