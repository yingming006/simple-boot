package com.example.simple.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流的唯一键。
     * 可以是固定的字符串，也可以是SpEL表达式。
     * 默认为空，表示使用方法签名作为key。
     */
    String key() default "";

    /**
     * 限流的时间窗口，单位为秒。
     */
    int time() default 60;

    /**
     * 在时间窗口内允许的最大请求数。
     */
    int count();

    /**
     * 限流的类型。
     */
    LimitType limitType() default LimitType.DEFAULT;

    /**
     * 限流提示信息。
     */
    String message() default "操作过于频繁，请稍后再试";

    enum LimitType {
        /**
         * 默认策略，针对方法进行限流。
         */
        DEFAULT,
        /**
         * 根据IP地址进行限流。
         */
        IP,
        /**
         * 根据登录用户ID进行限流（需要用户已登录）。
         */
        USER_ID
    }
}