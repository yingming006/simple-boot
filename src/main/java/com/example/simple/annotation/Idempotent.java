package com.example.simple.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个API方法需要进行幂等性校验。
 * <p>
 * 此注解会自动为 Swagger UI 添加一个名为 'Idempotent-Token' 的请求头参数文档。
 * 调用者应首先通过 GET /auth/idempotent-token 接口获取令牌。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "Idempotent-Token",
        description = "幂等性令牌，用于防止重复提交。请通过 GET /auth/idempotent-token 接口获取。",
        in = ParameterIn.HEADER,
        required = true,
        schema = @Schema(type = "string")
)
public @interface Idempotent {
}