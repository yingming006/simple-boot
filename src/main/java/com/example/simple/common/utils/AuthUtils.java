package com.example.simple.common.utils;

import com.example.simple.interceptor.LoginUser;
import com.example.simple.interceptor.UserContext;

/**
 * 权限判断工具类
 */
public class AuthUtils {

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static boolean isSuperAdmin() {
        LoginUser loginUser = UserContext.getLoginUser();
        return loginUser != null && ROLE_SUPER_ADMIN.equals(loginUser.getRole());
    }

    /**
     * 判断当前登录用户是否为管理员
     * @return true 如果是管理员，否则 false
     */
    public static boolean isAdmin() {
        LoginUser loginUser = UserContext.getLoginUser();
        return loginUser != null && ROLE_ADMIN.equals(loginUser.getRole());
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        return UserContext.getUserId();
    }

    /**
     * 获取当前登录用户的角色
     * @return 用户的角色字符串，如果未登录则返回 null
     */
    public static String getUserRole() {
        return UserContext.getUserRole();
    }
}