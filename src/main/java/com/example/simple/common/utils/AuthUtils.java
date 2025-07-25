package com.example.simple.common.utils;

import com.example.simple.framework.security.principal.SecurityPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    /**
     * 获取当前登录的用户信息对象
     * @return LoginUser, 如果未登录则返回null
     */
    public static SecurityPrincipal getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityPrincipal) {
            return (SecurityPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 判断当前用户是否为超级管理员
     * @return boolean
     */
    public static boolean isSuperAdmin() {
        SecurityPrincipal loginUser = getLoginUser();
        if (loginUser == null || loginUser.getAuthorities() == null) {
            return false;
        }
        // 检查用户的权限集合中是否包含 "ROLE_SUPER_ADMIN"
        return loginUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + ROLE_SUPER_ADMIN));
    }

    /**
     * 判断当前用户是否为管理员
     * @return boolean
     */
    public static boolean isAdmin() {
        SecurityPrincipal loginUser = getLoginUser();
        if (loginUser == null || loginUser.getAuthorities() == null) {
            return false;
        }
        // 检查用户的权限集合中是否包含 "ROLE_ADMIN"
        return loginUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + ROLE_ADMIN));
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        SecurityPrincipal loginUser = getLoginUser();
        return (loginUser != null) ? loginUser.getId() : null;
    }

}