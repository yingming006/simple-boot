package com.example.simple.framework.validation.annotation;

import com.example.simple.framework.validation.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义校验注解：验证用户名是否在数据库中唯一。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {

    String message() default "用户名已存在";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}