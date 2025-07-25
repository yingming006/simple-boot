package com.example.simple.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个Controller方法或整个Controller为公开访问，无需认证。
 * <p>
 * 此注解将被系统自动扫描，并将其对应的URL路径注册为Spring Security的公开访问路径。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthIgnore {
}