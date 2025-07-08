package com.example.simple.common;

import lombok.Data;

@Data
public class GlobalResponse<T> {

    private Integer code;
    private String message;
    private T data;

    private GlobalResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static GlobalResponse<Void> success() {
        return success(null);
    }

    public static GlobalResponse<Void> error(Integer code, String message) {
        return new GlobalResponse<>(code, message, null);
    }

    public static GlobalResponse<Void> error(String message) {
        return new GlobalResponse<>(ResponseCode.ERROR.getCode(), message, null);
    }

    public static GlobalResponse<Void> error(ResponseCode responseCode) {
        return new GlobalResponse<>(responseCode.getCode(), responseCode.getMessage(), null);
    }
}
