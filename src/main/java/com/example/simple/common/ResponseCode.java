package com.example.simple.common;


import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(200, "操作成功"),
    ERROR(1000, "操作失败");

    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}