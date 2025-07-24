package com.example.simple.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    ERROR(500, "系统未知异常");

    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}