package com.example.simple.framework.web.annotation;

import com.example.simple.config.web.xss.XssDeserializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于对请求的JSON数据中的字符串字段进行XSS净化。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = XssDeserializer.class)
public @interface XssSafe {

    XssMode mode() default XssMode.STRIP;

    enum XssMode {
        /** 剥离所有HTML标签 (默认) */
        STRIP,
        /** 保留安全的富文本HTML标签 */
        RICH_TEXT
    }
}