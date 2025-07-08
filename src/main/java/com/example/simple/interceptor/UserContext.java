package com.example.simple.interceptor;


public class UserContext {
    private static final ThreadLocal<LoginUser> userHolder = new ThreadLocal<>();

    public static void setLoginUser(LoginUser loginUser) {
        userHolder.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return userHolder.get();
    }

    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    public static String getUserRole() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRole() : null;
    }

    public static void remove() {
        userHolder.remove();
    }
}