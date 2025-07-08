package com.example.simple.exception;

import com.example.simple.common.GlobalResponse;
import com.example.simple.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public GlobalResponse<Void> handleBusinessException(BusinessException e, HttpServletResponse response) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());

        switch (e.getCode()) {
            case 401:
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                break;
            case 403:
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
            case 429: // 处理限流异常
                // response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                response.setStatus(429);
                break;
            default:
                break;
        }

        return GlobalResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public GlobalResponse<Void> handleException(Exception e, HttpServletResponse response) {
        log.error("系统未知异常: ", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return GlobalResponse.error(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
    }

    /**
     * 处理 @Valid 注解校验失败的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GlobalResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验失败: {}", errorMessage);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return GlobalResponse.error(400, errorMessage);
    }
}